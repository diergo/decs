package diergo.csv;

import org.junit.Test;

import static diergo.csv.CsvParserBuilder.csvParser;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class CsvParserBuilderTest {

    @Test
    public void byDefaultALineParserIsCreated() throws ReflectiveOperationException {
        RowParser parser = (RowParser) csvParser().build();

        assertThat(parser.quote, is('"'));
        assertThat(parser.commentStart, nullValue());
    }

    @Test(expected = IllegalStateException.class)
    public void byDefaultAnAutoSeparatorDeterminerIsConfiguredWhichCannotHandleAnEmptyLine() throws ReflectiveOperationException {
        RowParser parser = (RowParser) csvParser().build();
        parser.determiner.apply("");
    }

    @Test
    public void allConfigurationsArePassedToParser() throws ReflectiveOperationException {
        RowParser parser = (RowParser) csvParser().commentsStartWith("#").quotedWith('\'').separatedBy(',').build();

        assertThat(parser.quote, is('\''));
        assertThat(parser.commentStart, is("#"));
        assertThat(parser.determiner.apply(""), is(','));
    }
}
