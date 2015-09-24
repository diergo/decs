package diergo.csv;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class CsvLineParserTest {

    private CsvLineParser parser;

    @Test
    public void emptyLineIsNoColumns() {
        assertThat(parse("\n"), is(new String[0]));
    }

    @Test
    public void commentsAreReadAsSingleColumns() {
        assertThat(parse("#comment;no columns"), is(new String[] {"#comment;no columns"}));
    }

    @Test
    public void separatedLineIsSplitted() {
        assertThat(parse("a;b;c"), is(new String[] {"a", "b", "c"}));
    }

    @Test
    public void quotedFieldWithSeparatorIsNotSplitted() {
        assertThat(parse("\"hi;ho\""), is(new String[] {"hi;ho"}));
    }

    @Test
    public void quotedFieldsAreUnquoted() {
        assertThat(parse("\"hi\";\"ho\""), is(new String[] {"hi","ho"}));
    }

    @Test
    public void quotedFieldWithQuotesIsUnquoted() {
        assertThat(parse("\"\"\"hi\"\"ho\"\"\""), is(new String[] {"\"hi\"ho\""}));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unquotedFieldWithQuotesIsIllegal() {
        parse("hi\"ho");
    }

    @Test
    public void quotedFieldWithMissingEndQuoteReturnsNullAndIsStoredInternallyAsAdditionalLine() {
        assertThat(parse("\"hi;"), nullValue());
        assertThat(parser.apply("ho\""), is(new String[] {"hi;\nho"}));
    }

    private String[] parse(String line) {
        return parse(";", '"', "#", line);
    }

    private String[] parse(CharSequence separators, char quote, String commentStart, String line) {
        parser = new CsvLineParser(separators, quote, commentStart);
        return parser.apply(line);
    }
}
