package diergo.csv;

import org.junit.Test;

import java.text.DecimalFormatSymbols;

import static diergo.csv.CsvPrinterBuilder.buildCsvPrinter;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvPrinterBuilderTest {

    @Test
    public void commentStartIdHashPerDefault() {
        RowPrinter printer = (RowPrinter) buildCsvPrinter().build();
        
        assertThat(printer.commentStart, is("#"));
    }

    @Test
    public void commentStartCanBeConfigured() {
        RowPrinter printer = (RowPrinter) buildCsvPrinter().commentsStartWith("//").build();

        assertThat(printer.commentStart, is("//"));
    }

    @Test
    public void quoteIsDoublePerDefault() {
        RowPrinter printer = (RowPrinter) buildCsvPrinter().build();

        assertThat(printer.quote, is('"'));
    }

    @Test
    public void quoteCanBeConfigured() {
        RowPrinter printer = (RowPrinter) buildCsvPrinter().quotedWith('\'').build();

        assertThat(printer.quote, is('\''));
    }

    @Test
    public void separatorIsPatternSeparatorForCurrentLocale() {
        RowPrinter printer = (RowPrinter) buildCsvPrinter().build();

        assertThat(printer.separator, is(DecimalFormatSymbols.getInstance().getPatternSeparator()));
    }

    @Test
    public void separatorCanBeConfigured() {
        RowPrinter printer = (RowPrinter) buildCsvPrinter().separatedBy('\t').build();

        assertThat(printer.separator, is('\t'));
    }
}
