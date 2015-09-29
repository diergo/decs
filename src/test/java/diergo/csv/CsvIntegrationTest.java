package diergo.csv;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static diergo.csv.CsvParserBuilder.csvParser;
import static diergo.csv.CsvPrinterBuilder.csvPrinter;
import static diergo.csv.CsvWriterCollector.toWriter;
import static diergo.csv.Maps.toMaps;
import static diergo.csv.Maps.toRowsWithHeader;
import static diergo.csv.Maps.withValuesMapped;
import static diergo.csv.Readers.asLines;
import static java.math.BigDecimal.ROUND_UNNECESSARY;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvIntegrationTest {

    private InputStreamReader csv;

    @Test
    public void csvCanBeReadAndMapped() throws IOException {
        List<Map<String, String>> rows = asLines(csv).map(csvParser().separatedBy(',').build()).flatMap(Collection::stream)
                .map(Rows::replaceEmptyWithNull).map(toMaps()).flatMap(Collection::stream).collect(toList());

        assertThat(rows.size(), is(5));
        for (Map<String, String> row : rows) {
            assertThat(row.keySet(), hasItems("Year", "Make", "Model", "Description", "Price"));
        }
        assertThat(rows.stream().map(Map::values).collect(toList()), hasItems(
                hasItems("1997", "Ford", "E350", "ac, abs, moon", "3000.00"),
                hasItems("1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00"),
                hasItems("1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00"),
                hasItems("1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00"),
                hasItems(null, null, "Venture \"Extended Edition\"", null, "4900.00")
        ));
    }

    @Test
    public void csvCanBeMappedAndWritten() {
        StringWriter out = Stream.<Map<String, Object>>builder()
                .add(createValues(1997, "Ford", "E350", "ac, abs, moon", 3000.0))
                .add(createValues(1999, "Chevy", "Venture \"Extended Edition\"", "", 4900.0))
                .add(createValues(1996, "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", 4799.0))
                .add(createValues(1999, "Chevy", "Venture \"Extended Edition, Very Large\"", null, 5000.0))
                .add(createValues(null, null, "Venture \"Extended Edition\"", null, 4900.0))
                .build()
                .map(withValuesMapped(value -> value == null ? null : String.valueOf(value)))
                .map(toRowsWithHeader())
                .flatMap(Collection::stream)
                .map(csvPrinter().separatedBy(',').build())
                .collect(toWriter(new StringWriter()));

        String expected = new Scanner(csv).useDelimiter("\\Z").next();
        assertThat(out.toString(), is(expected.replaceAll(",\"\",", ",,") + '\n'));
    }

    @Before
    /**
     * Prepares a reader to get data from https://raw.githubusercontent.com/uniVocity/csv-parsers-comparison/master/src/main/resources/correctness.csv
     */
    public void preareCsvResource() {
        csv = new InputStreamReader(getClass().getResourceAsStream("/correctness.csv"), StandardCharsets.ISO_8859_1);
    }

    private Map<String, Object> createValues(Integer year, String make, String model, String description, double price) {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("Year", year);
        values.put("Make", make);
        values.put("Model", model);
        values.put("Description", description);
        values.put("Price", BigDecimal.valueOf(price).setScale(2, ROUND_UNNECESSARY));
        return values;
    }
}
