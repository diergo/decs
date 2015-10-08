package diergo.csv;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static diergo.csv.CsvParserBuilder.csvParser;
import static diergo.csv.ErrorHandlers.ignoreErrors;
import static diergo.csv.Readers.asLines;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(DataProviderRunner.class)
public class CsvReaderPerformanceTest {

    public static final String MAXMIND_WORLD_CITIES_POP = "/worldcitiespop.txt";

    @Test
    @DataProvider({"true,false,2500","false,false,2500","true,true,1000","false,true,1000"})
    public void readMillions(boolean usingFlatMap, boolean parallel, long maxTime) throws IOException {
        String kind = (usingFlatMap ? "using flat map" : "using filter and map")
            + ", " + (parallel ? "parallel" : "sequential");
        System.out.println("starting dry run " + kind + "…");
        runOnce(usingFlatMap, parallel);
        long[] times = new long[6];
        rangeClosed(1, times.length).forEachOrdered(loop -> {
            try {
                System.out.print(String.format("loop %d", loop));
                long[] timeAndCount = runOnce(usingFlatMap, parallel);
                times[loop - 1] = timeAndCount[0];
                System.out.println(String.format(" took %dms to read %d rows", timeAndCount[0], timeAndCount[1]));
                assertThat(timeAndCount[1], greaterThan(3000000L));
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
        double average = LongStream.of(times).average().getAsDouble();
        System.out.println(String.format("average %s is %.0fms", kind, average));
        assertThat("acceptable average[ms] for " + kind, average, lessThan((double)maxTime));
    }
    
    private long[] runOnce(boolean usingFlatMap, boolean parallel) throws IOException {
        InputStreamReader worldCitiesPopulation = new InputStreamReader(getClass().getResourceAsStream(MAXMIND_WORLD_CITIES_POP), UTF_8);
        long start = System.currentTimeMillis();
        Stream<String> lines = asLines(worldCitiesPopulation);
        if (parallel) {
            lines = lines.parallel();
        }
        Stream<List<Row>> intermediate = lines
                .map(csvParser().separatedBy(',').handlingErrors(ignoreErrors()).build());
        long count = (usingFlatMap ? intermediate.flatMap(Collection::stream) :
                intermediate.filter(rows -> !rows.isEmpty()).map(rows -> rows.get(0)))
            .count();
        long durations = System.currentTimeMillis() - start;
        try {
            return new long[]{durations, count};
        } finally {
            worldCitiesPopulation.close();
        }
    }
}
