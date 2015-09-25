package diergo.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.function.Function;
import java.util.stream.Stream;

import static diergo.csv.Row.DEFAULT_COMMENT_START;
import static diergo.csv.Row.DEFAULT_QUOTE;

public class CsvReaderBuilder {

    public static final String DEFAULT_SEPARATORS = ",;\t";

    public static CsvReaderBuilder toCsvStream(Stream<String> lines) {
        return new CsvReaderBuilder(lines);
    }

    public static CsvReaderBuilder toCsvStream(Reader in) {
        BufferedReader reader = BufferedReader.class.isInstance(in) ? BufferedReader.class.cast(in) : new BufferedReader(in);
        return toCsvStream(reader.lines()).closing(reader);
    }

    private final Stream<String> in;
    private Function<CsvReaderBuilder, Function<String, Row>> parserFactory;
    private CharSequence separators = DEFAULT_SEPARATORS;
    private char quote = DEFAULT_QUOTE;
    private String commentStart = DEFAULT_COMMENT_START;
    private boolean laxMode = false;
    private AutoCloseable toClose = null;

    private CsvReaderBuilder(Stream<String> in) {
        this.in = in;
        parserFactory = CsvReaderBuilder::createParser;
    }

    public CsvReaderBuilder quotedWith(char quote) {
        this.quote = quote;
        return this;
    }

    public CsvReaderBuilder commentsStartWith(String commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    public CsvReaderBuilder laxMode() {
        this.laxMode = true;
        return this;
    }

    public CsvReaderBuilder separatedBy(char separator) {
        this.separators = String.valueOf(separator);
        return this;
    }

    public CsvReaderBuilder separatedByAnyOf(CharSequence possibleSeparators) {
        this.separators = possibleSeparators;
        return this;
    }

    public CsvReaderBuilder closing(AutoCloseable toClose) {
        this.toClose = toClose;
        return this;
    }

    CsvReaderBuilder usingParser(Function<CsvReaderBuilder, Function<String, Row>> parserFactory) {
        this.parserFactory = parserFactory;
        return this;
    }

    public Stream<Row> build() {
        Stream<Row> csv = in.map(parserFactory.apply(CsvReaderBuilder.this)).filter(fields -> fields != null);
        if (toClose != null) {
            csv.onClose(() -> {
                    try {
                        toClose.close();
                    } catch (Exception e) {
                        throw new IllegalStateException("Cannot close underlying stream", e);
                    }
                }
            );
        }
        return csv;
    }

    private Function<String, Row> createParser() {
        return new RowParser(separators, quote, commentStart, laxMode);
    }
}
