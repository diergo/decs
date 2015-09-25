package diergo.csv;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static diergo.csv.CsvReaderBuilder.toCsvStream;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvReaderCompatibilityIntegrationTest {

    public static final String UNI_VELOCITY_CORRECTNESS =
        "https://raw.githubusercontent.com/uniVocity/csv-parsers-comparison/master/src/main/resources/correctness.csv";
    public static final String MAXMIND_WORLD_CITIES_POP =
        "http://www.maxmind.com/download/worldcities/worldcitiespop.txt.gz";

    @Test
    public void readerIsCompatibleToUniVocityData() throws IOException {
        InputStream in = new URL(UNI_VELOCITY_CORRECTNESS).openStream();
        InputStreamReader csv = new InputStreamReader(in, StandardCharsets.ISO_8859_1);

        List<Row> rows = toCsvStream(csv).separatedBy(',').treatEmptyAsNull().build().collect(toList());

        assertThat(rows.size(), is(6));
        assertThat(rows, is(asList(
            new Columns("Year", "Make", "Model", "Description", "Price"),
            new Columns("1997", "Ford", "E350", "ac, abs, moon", "3000.00"),
            new Columns("1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00"),
            new Columns("1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00"),
            new Columns("1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00"),
            new Columns(null, null, "Venture \"Extended Edition\"", null, "4900.00")
        )));
    }

    @Test
    public void readerHandlesHugeAmountOfData() throws IOException {
        InputStream in = new GZIPInputStream(new URL(MAXMIND_WORLD_CITIES_POP).openStream());
        InputStreamReader worldCitiesPopulation = new InputStreamReader(in, StandardCharsets.UTF_8);

        Map<Integer, Long> countByColumns = toCsvStream(worldCitiesPopulation).separatedBy(',').laxMode().treatEmptyAsNull().build().collect(Collectors.groupingBy(row -> row.getLength(), Collectors.counting()));

        assertThat(countByColumns.size(), is(1));
        assertThat(countByColumns.get(7), greaterThan(3000000L));
    }
}
