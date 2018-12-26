package diergo.csv;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

import static diergo.csv.CsvParserBuilder.csvParser;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

class CsvParserBuilderTest {

    @Test
    void byDefaultALineParserIsCreated() {
        RowParser parser = (RowParser) csvParser().build();

        assertThat(parser.quote, is('"'));
        assertThat(parser.commentStart, nullValue());
        assertThat(parser.laxMode, is(false));
        try {
            parser.errorHandler.apply(new RuntimeException(), "");
            fail("parsing error not thrown");
        } catch (RuntimeException expected) {
            // ok
        }
    }

    @Test
    void byDefaultAnAutoSeparatorDeterminerIsConfiguredWhichCannotHandleAnEmptyLine() {
        RowParser parser = (RowParser) csvParser().build();
        assertThrows(IllegalStateException.class, () -> parser.determiner.apply(""));
    }

    @Test
    void allConfigurationsArePassedToParser() {
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
