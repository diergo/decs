package diergo.csv;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RowParserTest {

    private RowParser parser;
    private BiFunction<RuntimeException, String, List<Row>> errorHandler;

    @Test
    public void emptyLineIsNoColumns() {
        assertThat(parse("\n"), is(new Columns()));
    }

    @Test
    public void thereAreNoCommentsByDefault() {
        assertThat(parse(",", '"', null, false, "#comment,no columns"), is(new Columns("#comment", "no columns")));
    }

    @Test
    public void commentsAreReadAsSingleColumns() {
        assertThat(parse(",", '"', "#", false, "#comment,no columns"), is(new Comment("comment,no columns")));
    }

    @Test
    public void separatedLineIsSplitted() {
        assertThat(parse("a,b,c"), is(new Columns("a", "b", "c")));
    }

    @Test
    public void quotedFieldWithSeparatorIsNotSplitted() {
        assertThat(parse("\"hi,ho\""), is(new Columns("hi,ho")));
    }

    @Test
    public void quotedFieldsAreUnquoted() {
        assertThat(parse("\"hi\",\"ho\""), is(new Columns("hi","ho")));
    }

    @Test
    public void quotedFieldWithQuotesIsUnquoted() {
        assertThat(parse("\"\"\"hi\"\"ho\"\"\",x"), is(new Columns("\"hi\"ho\"", "x")));
    }

    @Test
    public void lineWithUnquotedFieldWithQuotesIsIllegalAndDelegatesToErrorHandler() {
        Comment handled = new Comment("error");
        when(errorHandler.apply(any(IllegalArgumentException.class), anyString()))
            .thenReturn(singletonList(handled));
        assertThat(parse("hi\"ho"), is(handled));

        ArgumentCaptor<RuntimeException> error = ArgumentCaptor.forClass(RuntimeException.class);
        ArgumentCaptor<String> line = ArgumentCaptor.forClass(String.class);
        verify(errorHandler).apply(error.capture(), line.capture());
        assertThat(error.getValue(), instanceOf(IllegalArgumentException.class));
        assertThat(error.getValue().getMessage(), Matchers.containsString("0:2"));
        assertThat(line.getValue(), is("hi\"ho"));
    }

    @Test
    public void lineWithUnquotedFieldWithQuotesIsToleratedInLaxMode() {
        assertThat(parse(",", '"', "#", true, "hi\"ho"), is(new Columns("hi\"ho")));
    }

    @Test
    public void quotedFieldWithMissingEndQuoteReturnsNullAndIsStoredInternallyAsAdditionalLine() {
        assertThat(parse("\"hi,"), nullValue());
        assertThat(parser.apply("ho\""), is(singletonList(new Columns("hi,\nho"))));
    }

    @Test
    public void theAutoSeparatorDeterminerCanBeConfigured() {
        String separators = ";: ,|";
        for (char separator : separators.toCharArray()) {
            assertThat(parse(separators, '"', null, false, "a" + separator + "b"), is(new Columns("a", "b")));
        }
    }
    
    @Before
    @SuppressWarnings("unchecked")
    public void createErrorHandler() {
        errorHandler = mock(BiFunction.class);
    }

    private Row parse(String line) {
        return parse(",", '"', null, false, line);
    }

    private Row parse(CharSequence separators, char quote, String commentStart, boolean laxMode, String line) {
        parser = new RowParser(separators, quote, commentStart, laxMode, errorHandler);
        List<Row> rows = parser.apply(line);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
