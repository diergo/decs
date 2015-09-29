package diergo.csv;

import org.junit.Test;

import static diergo.csv.LoggingCsvParserErrorHandler.loggingErrors;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LoggingCsvParserErrorHandlerTest {

    @Test
    public void handlerReturnsNoRows() {
        assertThat(loggingErrors().apply(new IllegalArgumentException("error"), "foo,bar").size(), is(0));
    }
}
