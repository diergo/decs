package diergo.csv;

import java.text.DecimalFormatSymbols;
import java.util.function.Function;
import java.util.stream.Collector;

import static diergo.csv.Row.DEFAULT_QUOTE;

/**
 * Configure and build a CSV printer. Typically this is used as a mapper for a stream of rows before a collector:
 *
 * rows.{@link java.util.stream.Stream#map(Function) map}({@link #csvPrinter()}.{@link #build()}).{@link java.util.stream.Stream#collect(Collector) collect(...)}
 *
 * @see java.util.stream.Stream#map(Function)
 */
public class CsvPrinterBuilder {

    public static CsvPrinterBuilder csvPrinter() {
        return new CsvPrinterBuilder();
    }

    private char separator = DecimalFormatSymbols.getInstance().getPatternSeparator();
    private char quote = DEFAULT_QUOTE;
    private String commentStart = null;
    
    private CsvPrinterBuilder() {
    }

    /**
     * Configures the separator between data columns in the line.
     * By default the pattern separator is used.
     *
     * @see DecimalFormatSymbols#getPatternSeparator()
     */
    public CsvPrinterBuilder separatedBy(char separator) {
        this.separator = separator;
        return this;
    }

    /**
     * Configure the quoting character for data containing separator or multiple lines or a quote itself.
     * The default is {@link Row#DEFAULT_QUOTE}.
     */
    public CsvPrinterBuilder quotedWith(char quote) {
        this.quote = quote;
        return this;
    }

    /**
     * Enables comments prefixed as configured here.
     */
    public CsvPrinterBuilder commentsStartWith(String commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    /**
     * Created a new configured printer.
     * @see java.util.stream.Stream#map(Function)
     */
    public Function<Row,String> build() {
        return new RowPrinter(separator, quote, commentStart);
    }
}
