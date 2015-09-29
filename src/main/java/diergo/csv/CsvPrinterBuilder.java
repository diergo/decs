package diergo.csv;

import java.text.DecimalFormatSymbols;
import java.util.function.Function;

import static diergo.csv.Row.DEFAULT_QUOTE;

public class CsvPrinterBuilder {

    public static CsvPrinterBuilder csvPrinter() {
        return new CsvPrinterBuilder();
    }

    private char separator = DecimalFormatSymbols.getInstance().getPatternSeparator();
    private char quote = DEFAULT_QUOTE;
    private String commentStart = null;

    public CsvPrinterBuilder separatedBy(char separator) {
        this.separator = separator;
        return this;
    }

    public CsvPrinterBuilder quotedWith(char quote) {
        this.quote = quote;
        return this;
    }

    public CsvPrinterBuilder commentsStartWith(String commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    public Function<Row,String> build() {
        return new RowPrinter(separator, quote, commentStart);
    }
}
