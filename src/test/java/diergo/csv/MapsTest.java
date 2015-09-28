package diergo.csv;

import org.junit.Test;

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
}
