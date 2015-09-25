package diergo.csv;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static diergo.csv.CsvReaderBuilder.toCsvStream;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
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

    private static Path UNIVOCITY_CORRECTNESS;
    private static Path WORLDS_CITIES_POP;

    @Test
    public void readerIsCompatibleToUniVocityData() throws IOException {
        InputStreamReader csv = new InputStreamReader(new FileInputStream(UNIVOCITY_CORRECTNESS.toFile()), StandardCharsets.ISO_8859_1);

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
        InputStreamReader worldCitiesPopulation = new InputStreamReader(new FileInputStream(WORLDS_CITIES_POP.toFile()), StandardCharsets.UTF_8);

        long start = System.currentTimeMillis();
        long count = toCsvStream(worldCitiesPopulation).separatedBy(',').laxMode().build().count();
        long time = (System.currentTimeMillis() - start);
        System.out.println("took " + time + " ms to read " + count + " rows. ");
        assertThat(count, greaterThan(3000000L));
    }

    @BeforeClass
    public static void cacheFilesLocally() throws IOException {
        WORLDS_CITIES_POP = Files.createTempFile("worldcitiespop", "txt");
        Files.copy(new GZIPInputStream(new URL(MAXMIND_WORLD_CITIES_POP).openStream()), WORLDS_CITIES_POP, REPLACE_EXISTING);

        UNIVOCITY_CORRECTNESS = Files.createTempFile("correctness", "csv");
        Files.copy(new URL(UNI_VELOCITY_CORRECTNESS).openStream(), UNIVOCITY_CORRECTNESS, REPLACE_EXISTING);
    }
}
