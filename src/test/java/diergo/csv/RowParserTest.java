package diergo.csv;

import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
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
    public void thereAreNoCommentsByDefault() {
        assertThat(parse(";", '"', null, false, "#comment;no columns"), is(new Columns("#comment", "no columns")));
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
        assertThat(parse("\"\"\"hi\"\"ho\"\"\";x"), is(new Columns("\"hi\"ho\"", "x")));
    }

    @Test
    public void lineWithUnquotedFieldWithQuotesIsIllegalAndReturnedAsTwoCommentsContainingTheLineAsSecondOne() {
        assertThat(parse("hi\"ho"), is(new Comment("hi\"ho")));
    }

    @Test
    public void lineWithUnquotedFieldWithQuotesIsToleratedInLaxMode() {
        assertThat(parse(";", '"', "#", true, "hi\"ho"), is(new Columns("hi\"ho")));
    }

    @Test
    public void quotedFieldWithMissingEndQuoteReturnsNullAndIsStoredInternallyAsAdditionalLine() {
        assertThat(parse("\"hi;"), nullValue());
        assertThat(parser.apply("ho\""), is(singletonList(new Columns("hi;\nho"))));
    }

    @Test
    public void theAutoSeparatorDeterminerCanBeConfigured() {
        String separators = ";: ,|";
        for (char separator : separators.toCharArray()) {
            assertThat(parse(separators, '"', "#", false, "a" + separator + "b"), is(new Columns("a", "b")));
        }
    }

    private Row parse(String line) {
        return parse(";", '"', "#", false, line);
    }

    private Row parse(CharSequence separators, char quote, String commentStart, boolean laxMode, String line) {
        parser = new RowParser(separators, quote, commentStart, laxMode);
        List<Row> rows = parser.apply(line);
        return rows.isEmpty() ? null : rows.get(rows.size() - 1);
    }
}
