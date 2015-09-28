package diergo.csv;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static diergo.csv.CsvParserBuilder.buildCsvParser;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvIntegrationTest {

    @Test
    public void csvCanBeMapped() throws IOException {
        InputStreamReader csv = new InputStreamReader(getClass().getResourceAsStream("/correctness.csv"), StandardCharsets.ISO_8859_1);

        List<Map<String,String>> rows = buildCsvParser(csv).separatedBy(',').build()
            .map(Rows::replaceEmptyWithNull).map(Maps.toMaps()).flatMap(Collection::stream).collect(toList());

        assertThat(rows.size(), is(5));
        for (Map<String,String> row : rows) {
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
}
