package diergo.csv;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class RowParserTest {

    private RowParser parser;

    @Test
    public void emptyLineIsNoColumns() {
        assertThat(parse("\n"), is(new Columns()));
    }

    @Test
    public void commentsAreReadAsSingleColumns() {
        assertThat(parse("#comment;no columns"), is(new Comment("comment;no columns")));
    }

    @Test
    public void separatedLineIsSplitted() {
        assertThat(parse("a;b;c"), is(new Columns("a", "b", "c")));
    }

    @Test
    public void quotedFieldWithSeparatorIsNotSplitted() {
        assertThat(parse("\"hi;ho\""), is(new Columns("hi;ho")));
    }

    @Test
    public void quotedFieldsAreUnquoted() {
        assertThat(parse("\"hi\";\"ho\""), is(new Columns("hi","ho")));
    }

    @Test
    public void quotedFieldWithQuotesIsUnquoted() {
        assertThat(parse("\"\"\"hi\"\"ho\"\"\""), is(new Columns("\"hi\"ho\"")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void unquotedFieldWithQuotesIsIllegal() {
        parse("hi\"ho");
    }

    @Test
    public void quotedFieldWithMissingEndQuoteReturnsNullAndIsStoredInternallyAsAdditionalLine() {
        assertThat(parse("\"hi;"), nullValue());
        assertThat(parser.apply("ho\""), is(new Columns("hi;\nho")));
    }

    @Test
    public void theAutoSeparatorDeterminerCanBeConfigured() {
        String separators = ";: ,|";
        for (char separator : separators.toCharArray()) {
            assertThat(parse(separators, '"', "#", "a" + separator + "b"), is(new Columns("a", "b")));
        }
    }

    private Row parse(String line) {
        return parse(";", '"', "#", line);
    }

    private Row parse(CharSequence separators, char quote, String commentStart, String line) {
        parser = new RowParser(separators, quote, commentStart);
        return parser.apply(line);
    }
}
