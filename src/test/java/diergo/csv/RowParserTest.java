package diergo.csv;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RowParserTest {

    private RowParser parser;
    private BiFunction<RuntimeException, String, List<Row>> errorHandler;

    @Test
    void emptyLineIsNoColumns() {
        assertThat(parse("\n"), is(new Cells()));
    }

    @Test
    void thereAreNoCommentsByDefault() {
        assertThat(parse(",", '"', null, false, "#comment,no columns"), is(new Cells("#comment", "no columns")));
    }

    @Test
    void commentsAreReadAsSingleColumns() {
        assertThat(parse(",", '"', "#", false, "#comment,no columns"), is(new Comment("comment,no columns")));
    }

    @Test
    void separatedLineIsSplitted() {
        assertThat(parse("a,b,c"), is(new Cells("a", "b", "c")));
    }

    @Test
    void quotedFieldWithSeparatorIsNotSplitted() {
        assertThat(parse("\"hi,ho\""), is(new Cells("hi,ho")));
    }

    @Test
    void quotedFieldsAreUnquoted() {
        assertThat(parse("\"hi\",\"ho\""), is(new Cells("hi", "ho")));
    }

    @Test
    void quotedFieldWithQuotesIsUnquoted() {
        assertThat(parse("\"\"\"hi\"\"ho\"\"\",x"), is(new Cells("\"hi\"ho\"", "x")));
    }

    @Test
    void lineWithUnquotedFieldWithQuotesIsIllegalAndDelegatesToErrorHandler() {
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
    void errorhandlingWithNoResultingRowIsHandledProperly() {
        when(errorHandler.apply(any(IllegalArgumentException.class), anyString()))
                .thenReturn(emptyList());
        assertThat(parse("hi\"ho,hi ho"), nullValue());
        assertThat(parser.apply("hi ho"), is(singletonList(new Cells("hi ho"))));
    }

    @Test
    void lineWithUnquotedFieldWithQuotesIsToleratedInLaxMode() {
        assertThat(parse(",", '"', "#", true, "hi\"ho"), is(new Cells("hi\"ho")));
    }

    @Test
    void quotedFieldWithMissingEndQuoteReturnsNullAndIsStoredInternallyAsAdditionalLine() {
        assertThat(parse("\"hi,"), nullValue());
        assertThat(parser.apply("ho\""), is(singletonList(new Cells("hi,\nho"))));
    }

    @Test
    void theAutoSeparatorDeterminerCanBeConfigured() {
        String separators = ";: ,|";
        for (char separator : separators.toCharArray()) {
            assertThat(parse(separators, '"', null, false, "a" + separator + "b"), is(new Cells("a", "b")));
        }
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void createErrorHandler() {
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
