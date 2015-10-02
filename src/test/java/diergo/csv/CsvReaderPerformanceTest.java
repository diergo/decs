package diergo.csv;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.LongStream;
import java.util.zip.GZIPInputStream;

import static diergo.csv.CsvParserBuilder.csvParser;
import static diergo.csv.ErrorHandlers.ignoreErrors;
import static diergo.csv.Readers.asLines;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNoException;

public class CsvReaderPerformanceTest {

    public static final String MAXMIND_WORLD_CITIES_POP =
        "http://www.maxmind.com/download/worldcities/worldcitiespop.txt.gz";

    private static Path WORLDS_CITIES_POP;

    @Test
    public void readerHandlesHugeAmountOfData() throws FileNotFoundException {
        System.out.println("starting dry run…");
        // one dry run before
        runOnce();
        long[] times = new long[6];
        rangeClosed(1, times.length).forEachOrdered(loop -> {
            try {
                System.out.print(String.format("loop %d", loop));
                long[] timeAndCount = runOnce();
                times[loop - 1] = timeAndCount[0];
                System.out.println(String.format(" took %dms to read %d rows", timeAndCount[0], timeAndCount[1]));
                assertThat(timeAndCount[1], greaterThan(3000000L));
            } catch (FileNotFoundException e) {
                fail(e.getMessage());
            }
        });
        double average = LongStream.of(times).average().getAsDouble();
        System.out.println(String.format("average is %.0fms", average));
        assertThat("acceptable average[ms]", average, lessThan(2000.0));
    }
    
    private long[] runOnce() throws FileNotFoundException {
        InputStreamReader worldCitiesPopulation = new InputStreamReader(new FileInputStream(WORLDS_CITIES_POP.toFile()), UTF_8);
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long start = threadMXBean.getCurrentThreadCpuTime();
        long count = asLines(worldCitiesPopulation)
            .map(csvParser().separatedBy(',').handlingErrors(ignoreErrors()).build()).flatMap(Collection::stream)
            .count();
        return new long[] {(threadMXBean.getCurrentThreadCpuTime() - start) / 1000000L, count};
    }

    @BeforeClass
    public static void cacheFilesLocally() throws IOException {
        WORLDS_CITIES_POP = Files.createTempFile("worldcitiespop", "txt");
        URL url = new URL(MAXMIND_WORLD_CITIES_POP);
        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(500);
            connection.setReadTimeout(2000);
            System.out.println(String.format("copying %s to local", MAXMIND_WORLD_CITIES_POP));
            Files.copy(new GZIPInputStream(connection.getInputStream()), WORLDS_CITIES_POP, REPLACE_EXISTING);
        } catch (IOException error) {
            System.out.print("cannot read " + MAXMIND_WORLD_CITIES_POP + ", performance test skipped");
            assumeNoException(error);
        }
    }
}
