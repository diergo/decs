package diergo.csv;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

import static diergo.csv.CsvParserBuilder.csvParser;
import static diergo.csv.CsvPrinterBuilder.csvPrinter;
import static diergo.csv.Maps.toMaps;
import static diergo.csv.Maps.toRowsWithHeader;
import static diergo.csv.Maps.withValuesMapped;
import static diergo.csv.Readers.asLines;
import static diergo.csv.Values.parsedValue;
import static diergo.csv.Writers.toWriter;
import static java.math.BigDecimal.ROUND_UNNECESSARY;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvIntegrationTest {

    private static final Map<String, Class<?>> VALUE_TYPES;

    // the data is originally from https://raw.githubusercontent.com/uniVocity/csv-parsers-comparison/master/src/main/resources/correctness.csv
    private static final String EXAMPLE_DATA = "/correctness.csv";

    static {
        VALUE_TYPES = new HashMap<>();
        VALUE_TYPES.put("Year", Integer.class);
        VALUE_TYPES.put("Price", Double.class);
    }
    
    private InputStreamReader csv;

    @Test
    public void csvCanBeReadAndMapped() throws IOException {
        List<Map<String, Object>> rows = asLines(csv)
            .map(csvParser().separatedBy(',').build()).flatMap(Collection::stream)
            .map(Rows::replaceEmptyWithNull)
            .map(toMaps()).flatMap(Collection::stream)
            .map(withValuesMapped(parsedValue(VALUE_TYPES)))
            .collect(toList());

        assertThat(rows.size(), is(5));
        for (Map<String, Object> row : rows) {
            assertThat(row.keySet(), hasItems("Year", "Make", "Model", "Description", "Price"));
        }
        assertThat(rows.stream().map(Map::values).collect(toList()), hasItems(
            hasItems(1997, "Ford", "E350", "ac, abs, moon", 3000.0),
            hasItems(1999, "Chevy", "Venture \"Extended Edition\"", null, 4900.0),
            hasItems(1996, "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", 4799.0),
            hasItems(1999, "Chevy", "Venture \"Extended Edition, Very Large\"", null, 5000.0),
            hasItems(null, null, "Venture \"Extended Edition\"", null, 4900.0)
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
            .map(withValuesMapped(Values::valueAsString))
            .map(toRowsWithHeader())
            .flatMap(Collection::stream)
            .map(csvPrinter().separatedBy(',').build())
            .collect(toWriter(new StringWriter()));

        assertThat(out.toString(), is(readData(csv).replaceAll(",\"\",", ",,")));
    }

    @Before
    public void prepareCsvResource() {
        csv = new InputStreamReader(getClass().getResourceAsStream(EXAMPLE_DATA), ISO_8859_1);
    }
    
    private String readData(Reader data) {
        String content = new Scanner(data).useDelimiter("\\Z").next();
        return content.endsWith("\n") ? content : (content + '\n');
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
