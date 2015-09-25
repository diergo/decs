package diergo.csv;

import java.util.function.Function;

import static diergo.csv.Row.DEFAULT_COMMENT_START;
import static diergo.csv.Row.DEFAULT_QUOTE;

public class CsvPrinterBuilder {

    public static CsvPrinterBuilder buildCsvPrinter() {
        return new CsvPrinterBuilder();
    }

    private char separator = ',';
    private char quote = DEFAULT_QUOTE;
    private String commentStart = DEFAULT_COMMENT_START;

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
