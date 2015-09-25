package diergo.csv;

import java.io.Writer;
import java.util.stream.Collector;

import static diergo.csv.Row.DEFAULT_COMMENT_START;
import static diergo.csv.Row.DEFAULT_QUOTE;

public class CsvWriterCollectorBuilder {

    public static CsvWriterCollectorBuilder collectToCsvWriter(Writer out) {
        return new CsvWriterCollectorBuilder(out);
    }

    private final Writer out;
    private char separator = ',';
    private char quote = DEFAULT_QUOTE;
    private String commentStart = DEFAULT_COMMENT_START;

    public CsvWriterCollectorBuilder(Writer out) {
        this.out = out;
    }

    public CsvWriterCollectorBuilder separatedBy(char separator) {
        this.separator = separator;
        return this;
    }

    public CsvWriterCollectorBuilder quotedWith(char quote) {
        this.quote = quote;
        return this;
    }

    public CsvWriterCollectorBuilder commentsStartWith(String commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    public Collector<Row, Writer, Writer> build() {
        return new CsvWriterCollector(out, new RowPrinter(separator, quote, commentStart));
    }
}
