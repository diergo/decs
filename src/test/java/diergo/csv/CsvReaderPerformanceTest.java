package diergo.csv;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.zip.GZIPInputStream;

import static diergo.csv.CsvParserBuilder.csvParser;
import static diergo.csv.ErrorHandlers.ignoreErrors;
import static diergo.csv.Readers.asLines;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeNoException;

public class CsvReaderPerformanceTest {

    public static final String MAXMIND_WORLD_CITIES_POP =
        "http://www.maxmind.com/download/worldcities/worldcitiespop.txt.gz";

    private static Path WORLDS_CITIES_POP;

    @Test
    public void readerHandlesHugeAmountOfData() throws IOException {
        InputStreamReader worldCitiesPopulation = new InputStreamReader(new FileInputStream(WORLDS_CITIES_POP.toFile()), StandardCharsets.UTF_8);

        long start = System.currentTimeMillis();
        long count = asLines(worldCitiesPopulation)
            .map(csvParser().separatedBy(',').handlingErrors(ignoreErrors()).build()).flatMap(Collection::stream)
            .count();
        long time = (System.currentTimeMillis() - start);
        System.out.println("took " + time + " ms to read " + count + " rows. ");
        assertThat(count, greaterThan(3000000L));
    }

    @BeforeClass
    public static void cacheFilesLocally() throws IOException {
        WORLDS_CITIES_POP = Files.createTempFile("worldcitiespop", "txt");
        URL url = new URL(MAXMIND_WORLD_CITIES_POP);
        try {
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(500);
            connection.setReadTimeout(1000);
            Files.copy(new GZIPInputStream(connection.getInputStream()), WORLDS_CITIES_POP, REPLACE_EXISTING);
        } catch (IOException error) {
            System.out.print("cannot read " + MAXMIND_WORLD_CITIES_POP + ", performance test skipped");
            assumeNoException(error); 
        }
    }
}
