package diergo.csv;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static diergo.csv.Maps.*;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static java.util.function.UnaryOperator.identity;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class MapsTest {

    @Test
    void mapIsCreatedFromRowWithPredefinedHeader() {
        List<Map<String, String>> result = toMaps(asList("one", "two")).apply(new Cells("1", "2"));
        assertThat(result.size(), is(1));
        Map<String, String> values = result.get(0);
        assertThat(values.get("one"), is("1"));
        assertThat(values.get("two"), is("2"));
    }

    @Test
    void mapIsCreatedFromRowWithHeaderFromFirstRow() {
        Function<Row, List<Map<String, String>>> mapper = toMaps();
        assertThat(mapper.apply(new Cells("one", "two")).size(), is(0));
        List<Map<String, String>> result = mapper.apply(new Cells("1", "2"));
        assertThat(result.size(), is(1));
        Map<String, String> values = result.get(0);
        assertThat(values.get("one"), is("1"));
        assertThat(values.get("two"), is("2"));
    }

    @Test
    void commentIsIgnoredAsMap() {
        assertThat(toMaps(singletonList("one")).apply(new Comment("what?")), is(emptyList()));
    }

    @Test
    void rowContainsOnlyColumnsOfPredefinedHeader() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("zero", "0");
        values.put("one", "1");
        values.put("two", "2");
        values.put("three", "3");
        Row result = toRows(asList("one", "two")).apply(values);
        assertThat(result, is(new Cells("1", "2")));
    }

    @Test
    void headerCanBeAdded() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("one", "1");
        values.put("two", "2");
        List<Row> result = toRowsWithHeader(asList("one", "two")).apply(values);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(new Cells("one", "two")));
        assertThat(result.get(1), is(new Cells("1", "2")));
    }

    @Test
    void headerCanBeExtractedFromValue() {
        Map<String, String> values = new LinkedHashMap<>();
        values.put("one", "1");
        values.put("two", "2");
        List<Row> result = toRowsWithHeader().apply(values);
        assertThat(result.size(), is(2));
        assertThat(result.get(0), is(new Cells("one", "two")));
        assertThat(result.get(1), is(new Cells("1", "2")));
    }
    
    @Test
    void valuesAreMapped() {
        Object mapped = new Object();
        assertThat(Maps.<String,Object>withValuesMapped(HashMap::new, (values, name) -> mapped).apply(singletonMap("test", "x")),
            is(singletonMap("test", mapped)));
    }

    @Test
    void valueIsRemoved() {
        Map<String,Integer> values = new HashMap<>();
        values.put("foo", 0);
        values.put("bar", 1);
        values.put("test", 2);
        Map<String, Integer> result = Maps.<Integer>removingValue(HashMap::new, "foo", "bar").apply(unmodifiableMap(values));
        assertThat(result.size(), is(1));
        assertThat(result, hasEntry("test", 2));
    }

    @Test
    void valueIsRemovedInPlace() {
        Map<String,Integer> values = new HashMap<>();
        values.put("foo", 0);
        values.put("bar", 1);
        values.put("test", 2);
        Map<String, Integer> result = Maps.<Integer>removingValue(identity(), "foo", "bar").apply(values);
        assertThat(result, sameInstance(values));
        assertThat(result.size(), is(1));
        assertThat(result, hasEntry("test", 2));
    }

    @Test
    void valueIsRenamed() {
        Map<String,Integer> values = new HashMap<>();
        values.put("foo", 0);
        values.put("bar", 1);
        Map<String, Integer> result = Maps.<Integer>renamingValue(HashMap::new, "bar", "test").apply(unmodifiableMap(values));
        assertThat(result.size(), is(2));
        assertThat(result, hasEntry("test", 1));
    }

    @Test
    void valueIsRenamedInPlace() {
        Map<String,Integer> values = new HashMap<>();
        values.put("foo", 0);
        values.put("bar", 1);
        Map<String, Integer> result = Maps.<Integer>renamingValue(identity(), "bar", "test").apply(values);
        assertThat(result, sameInstance(values));
        assertThat(result.size(), is(2));
        assertThat(result, hasEntry("test", 1));
    }

    @Test
    void valueIsAdded() {
        assertThat(addingValue(HashMap::new, "test", any -> 1).apply(singletonMap("foo", 0)),
                allOf(hasEntry("foo", 0), hasEntry("test", 1)));
    }

    @Test
    void valueIsAddedInPlace() {
        Map<String,Integer> values = new HashMap<>();
        values.put("foo", 0);
        Function<Map<String, Integer>, ? extends Integer> valueCreator = any -> 1;
        Map<String, Integer> result = addingValue(identity(), "test", valueCreator).apply(values);
        assertThat(result, sameInstance(values));
        assertThat(result, allOf(hasEntry("foo", 0), hasEntry("test", 1)));
    }
}
