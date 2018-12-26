package diergo.csv;

import org.junit.jupiter.api.Test;

import java.util.List;

import static diergo.csv.ErrorHandlers.ignoreErrors;
import static diergo.csv.ErrorHandlers.commentingErrors;
import static diergo.csv.ErrorHandlers.loggingErrors;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ErrorHandlersTest {

    @Test
    void ignoringCreatesNoRows() {
        List<Row> result = ignoreErrors().apply(new IllegalArgumentException("error"), "foo,bar");
        assertThat(result, is(emptyList()));
    }

    @Test
    void commentingCreatesTwoComments() {
        List<Row> result = commentingErrors().apply(new IllegalArgumentException("error"), "foo,bar");
        assertThat(result, is(asList(new Comment("error"), new Comment("foo,bar"))));
    }

    @Test
    void loggingCreatesNoRows() {
        List<Row> result = loggingErrors().apply(new IllegalArgumentException("error"), "foo,bar");
        assertThat(result, is(emptyList()));
    }
}
