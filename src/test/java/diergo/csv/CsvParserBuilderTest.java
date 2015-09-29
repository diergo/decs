package diergo.csv;

import org.junit.Test;

import java.util.List;
import java.util.function.BiFunction;

import static diergo.csv.CsvParserBuilder.csvParser;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class CsvParserBuilderTest {

    @Test
    public void byDefaultALineParserIsCreated() throws ReflectiveOperationException {
        RowParser parser = (RowParser) csvParser().build();

        assertThat(parser.quote, is('"'));
        assertThat(parser.commentStart, nullValue());
        assertThat(parser.laxMode, is(false));
        assertThat(parser.errorHandler, notNullValue());
    }

    @Test
    public void withoutCommentsErrorsAreLogged() throws ReflectiveOperationException {
        RowParser parser = (RowParser) csvParser().build();

        assertThat(parser.commentStart, nullValue());
        assertThat(parser.errorHandler, instanceOf(LoggingCsvParserErrorHandler.class));
    }

    @Test
    public void withCommentsErrorsAreCommented() throws ReflectiveOperationException {
        RowParser parser = (RowParser) csvParser().commentsStartWith("#").build();

        assertThat(parser.commentStart, is("#"));
        assertThat(parser.errorHandler, instanceOf(CommentingCsvParserErrorHandler.class));
    }

    @Test(expected = IllegalStateException.class)
    public void byDefaultAnAutoSeparatorDeterminerIsConfiguredWhichCannotHandleAnEmptyLine() throws ReflectiveOperationException {
        RowParser parser = (RowParser) csvParser().build();
        parser.determiner.apply("");
    }

    @Test
    public void allConfigurationsArePassedToParser() throws ReflectiveOperationException {
        @SuppressWarnings("unchecked")
        BiFunction<RuntimeException, String, List<Row>> errorHandler = mock(BiFunction.class);
        RowParser parser = (RowParser) csvParser().commentsStartWith("#").quotedWith('\'').separatedBy(',').inLaxMode().handlingErrors(errorHandler).build();

        assertThat(parser.quote, is('\''));
        assertThat(parser.commentStart, is("#"));
        assertThat(parser.determiner.apply(""), is(','));
        assertThat(parser.laxMode, is(true));
        assertThat(parser.errorHandler, is(errorHandler));
    }
}
