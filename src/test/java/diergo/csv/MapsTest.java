package diergo.csv;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MapsTest {

    @Test
    public void mapIsCreatedFromRowWithPredefinedHeader() {
        List<Map<String, String>> result = Maps.toMaps(asList("one", "two")).apply(new Columns("1", "2"));
        assertThat(result.size(), is(1));
        Map<String, String> values = result.get(0);
        assertThat(values.get("one"), is("1"));
        assertThat(values.get("two"), is("2"));
    }

    @Test
    public void mapIsCreatedFromRowWithHeaderFromFirstRow() {
        Function<Row, List<Map<String, String>>> mapper = Maps.toMaps();
        assertThat(mapper.apply(new Columns("one", "two")).size(), is(0));
        List<Map<String, String>> result = mapper.apply(new Columns("1", "2"));
        assertThat(result.size(), is(1));
        Map<String, String> values = result.get(0);
        assertThat(values.get("one"), is("1"));
        assertThat(values.get("two"), is("2"));
    }

    @Test
    public void commentIsIgnoredAsMap() {
        assertThat(Maps.toMaps(singletonList("one")).apply(new Comment("what?")), is(emptyList()));
    }

    @Test
    public void rowContainsOnlyColumnsOfPredefinedHeader() {
        Map<String, String> values = new HashMap<>();
        values.put("zero", "0");
        values.put("one", "1");
        values.put("two", "2");
        values.put("three", "3");
        List<Row> result = Maps.toRows(false, asList("one", "two")).apply(values);
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(new Columns("1", "2")));
    }

    @Test
    public void headerCanBeAdded() {
        Map<String, String> values = new HashMap<>();
        values.put("one", "1");
        values.put("two", "2");
        List<Row> result = Maps.toRows(true, asList("one", "two")).apply(values);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(new Columns("one", "two")));
        assertThat(result.get(1), is(new Columns("1", "2")));
    }

    @Test
    public void headerCanBeExtractedFromValuee() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("one", "1");
        values.put("two", "2");
        List<Row> result = Maps.toRows(true).apply(values);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(new Columns("one", "two")));
        assertThat(result.get(1), is(new Columns("1", "2")));
    }
}
