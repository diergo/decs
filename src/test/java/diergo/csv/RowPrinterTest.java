package diergo.csv;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class RowPrinterTest {

    @Test
    void columnsArePrintedAsSeparatedLine() {
        assertThat(printRow(new Cells("foo", "bar")), is("foo,bar"));
    }

    @Test
    void columnsAreQuotedWhenIncludingQuoteSeparatorOrNewline() {
        assertThat(printRow(new Cells("f,oo", "ba\"r", "foo\nbar")), is("\"f,oo\",\"ba\"\"r\",\"foo\nbar\""));
    }

    @Test
    void nullColumnsIsNotPrinted() {
        assertThat(printRow(new Cells("foo", null, "bar")), is("foo,,bar"));
    }

    @Test
    void commentIsPrintedAsPrefixedLine() {
        assertThat(printRow(new Comment("what?"), "#"), is("#what?"));
    }

    private String printRow(Row row) {
        return printRow(row, null);
    }

    private String printRow(Row row, String commentStart) {
        return new RowPrinter(',', '"', commentStart).apply(row);
    }
}
