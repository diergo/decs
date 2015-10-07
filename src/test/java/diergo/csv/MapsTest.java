package diergo.csv;

import org.junit.Test;

import java.util.*;
import java.util.function.Function;

import static diergo.csv.Maps.*;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MapsTest {

    @Test
    public void mapIsCreatedFromRowWithPredefinedHeader() {
        List<Map<String, String>> result = toMaps(asList("one", "two")).apply(new Cells("1", "2"));
        assertThat(result.size(), is(1));
        Map<String, String> values = result.get(0);
        assertThat(values.get("one"), is("1"));
        assertThat(values.get("two"), is("2"));
    }

    @Test
    public void mapIsCreatedFromRowWithHeaderFromFirstRow() {
        Function<Row, List<Map<String, String>>> mapper = toMaps();
        assertThat(mapper.apply(new Cells("one", "two")).size(), is(0));
        List<Map<String, String>> result = mapper.apply(new Cells("1", "2"));
        assertThat(result.size(), is(1));
        Map<String, String> values = result.get(0);
        assertThat(values.get("one"), is("1"));
        assertThat(values.get("two"), is("2"));
    }

    @Test
    public void commentIsIgnoredAsMap() {
        assertThat(toMaps(singletonList("one")).apply(new Comment("what?")), is(emptyList()));
    }

    @Test
    public void rowContainsOnlyColumnsOfPredefinedHeader() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("zero", "0");
        values.put("one", "1");
        values.put("two", "2");
        values.put("three", "3");
        List<Row> result = toRows(asList("one", "two")).apply(values);
        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(new Cells("1", "2")));
    }

    @Test
    public void headerCanBeAdded() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("one", "1");
        values.put("two", "2");
        List<Row> result = toRowsWithHeader(asList("one", "two")).apply(values);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(new Cells("one", "two")));
        assertThat(result.get(1), is(new Cells("1", "2")));
    }

    @Test
    public void headerCanBeExtractedFromValue() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("one", "1");
        values.put("two", "2");
        List<Row> result = toRowsWithHeader().apply(values);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(new Cells("one", "two")));
        assertThat(result.get(1), is(new Cells("1", "2")));
    }
    
    @Test
    public void valuesAreMapped() {
        Object mapped = new Object();
        assertThat(withValuesMapped((values, name) -> mapped).apply(singletonMap("test", "x")),
            is(singletonMap("test", mapped)));
    }

    @Test
    public void valueIsRemoved() {
        Map<String,Integer> values = new HashMap<>();
        values.put("foo", 0);
        values.put("bar", 1);
        Map<String, Integer> result = Maps.<Integer>removingValue("foo").apply(unmodifiableMap(values));
        assertThat(result.size(), is(1));
        assertThat(result, hasEntry("bar", 1));
    }

    @Test
    public void valueIsRemovedInPlace() {
        Map<String,Integer> values = new HashMap<>();
        values.put("foo", 0);
        values.put("bar", 1);
        Map<String, Integer> result = Maps.<Integer>removingValueInPlace("foo").apply(values);
        assertThat(result, is(values));
        assertThat(result.size(), is(1));
        assertThat(result, hasEntry("bar", 1));
    }

    @Test
    public void valueIsAdded() {
        assertThat(addingValue("test", any -> 1).apply(singletonMap("foo", 0)),
                allOf(hasEntry("foo", 0), hasEntry("test", 1)));
    }

    @Test
    public void valueIsAddedInPlace() {
        Map<String,Integer> values = new HashMap<>();
        values.put("foo", 0);
        Map<String, Integer> result = Maps.<Integer>addingValueInPlace("test", any -> 1).apply(values);
        assertThat(result, is(values));
        assertThat(result, allOf(hasEntry("foo", 0), hasEntry("test", 1)));
    }
}
