package diergo.csv;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static diergo.csv.ErrorHandlers.ignoreErrors;
import static diergo.csv.ErrorHandlers.commentingErrors;
import static diergo.csv.ErrorHandlers.loggingErrors;
import static diergo.csv.ErrorHandlers.throwingError;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ErrorHandlersTest {

    @Test
    void throwingStopsParsing() {
        assertThrows(IllegalArgumentException.class, () -> {
            throwingError().apply("foo,bar", new IllegalArgumentException("error"));
        });
    }

    @Test
    void ignoringCreatesNoRows() {
        List<Row> result = ignoreErrors().apply("foo,bar", new IllegalArgumentException("error"));
        assertThat(result, is(emptyList()));
    }

    @Test
    void commentingCreatesTwoComments() {
        List<Row> result = commentingErrors().apply("foo,bar", new IllegalArgumentException("error"));
        assertThat(result, is(asList(new Comment("error"), new Comment("foo,bar"))));
    }

    @Test
    void loggingCreatesNoRows() {
        Map<String,RuntimeException> errors = new HashMap<>();
        IllegalArgumentException error = new IllegalArgumentException("error");
        List<Row> result = loggingErrors(errors::put).apply("foo,bar", error);
        assertThat(result, is(emptyList()));
        assertThat(errors, hasEntry("foo,bar", error));
    }
}
