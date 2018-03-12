package diergo.csv;

import org.junit.jupiter.api.Test;

import java.text.DecimalFormatSymbols;

import static diergo.csv.CsvPrinterBuilder.csvPrinter;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CsvPrinterBuilderTest {

    @Test
    public void noCommentStartIdPerDefault() {
        RowPrinter printer = (RowPrinter) csvPrinter().build();

        assertThat(printer.commentStart, nullValue());
    }

    @Test
    public void commentStartCanBeConfigured() {
        RowPrinter printer = (RowPrinter) csvPrinter().commentsStartWith("//").build();

        assertThat(printer.commentStart, is("//"));
    }

    @Test
    public void quoteIsDoublePerDefault() {
        RowPrinter printer = (RowPrinter) csvPrinter().build();

        assertThat(printer.quote, is('"'));
    }

    @Test
    public void quoteCanBeConfigured() {
        RowPrinter printer = (RowPrinter) csvPrinter().quotedWith('\'').build();

        assertThat(printer.quote, is('\''));
    }

    @Test
    public void separatorIsPatternSeparatorForCurrentLocale() {
        RowPrinter printer = (RowPrinter) csvPrinter().build();

        assertThat(printer.separator, is(DecimalFormatSymbols.getInstance().getPatternSeparator()));
    }

    @Test
    public void separatorCanBeConfigured() {
        RowPrinter printer = (RowPrinter) csvPrinter().separatedBy('\t').build();

        assertThat(printer.separator, is('\t'));
    }
}
