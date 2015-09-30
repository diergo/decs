package diergo.csv;

import org.junit.Test;

import java.util.List;

import static diergo.csv.ErrorHandlers.ignoreErrors;
import static diergo.csv.ErrorHandlers.commentingErrors;
import static diergo.csv.ErrorHandlers.loggingErrors;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ErrorHandlersTest {

    @Test
    public void ignoringCreatesNoRows() {
        List<Row> result = ignoreErrors().apply(new IllegalArgumentException("error"), "foo,bar");
        assertThat(result, is(emptyList()));
    }

    @Test
    public void commentingCreatesTwoComments() {
        List<Row> result = commentingErrors().apply(new IllegalArgumentException("error"), "foo,bar");
        assertThat(result, is(asList(new Comment("error"), new Comment("foo,bar"))));
    }

    @Test
    public void loggingCreatesNoRows() {
        List<Row> result = loggingErrors().apply(new IllegalArgumentException("error"), "foo,bar");
        assertThat(result, is(emptyList()));
    }
}
