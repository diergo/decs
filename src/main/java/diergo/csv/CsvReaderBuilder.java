package diergo.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class CsvReaderBuilder {

    public static final char DEFAULT_QUOTE = '"';
    public static final String DEFAULT_COMMENT_START = "#";
    public static final String DEFAULT_SEPARATORS = ",;\t";

    public static CsvReaderBuilder toCsvStream(Stream<String> lines) {
        return new CsvReaderBuilder(lines);
    }

    public static CsvReaderBuilder toCsvStream(Reader in) {
        BufferedReader reader = BufferedReader.class.isInstance(in) ? BufferedReader.class.cast(in) : new BufferedReader(in);
        return toCsvStream(reader.lines());
    }

    private final Stream<String> in;
    private Function<CsvReaderBuilder, Function<String, Row>> parserFactory;
    private CharSequence separators = DEFAULT_SEPARATORS;
    private char quote = DEFAULT_QUOTE;
    private String commentStart = DEFAULT_COMMENT_START;
    private boolean laxMode = false;
    private boolean skipComments = false;
    private boolean trimValues = false;
    private boolean treatEmptyAsNull = false;

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

    public CsvReaderBuilder skipComments() {
        skipComments = true;
        return this;
    }

    public CsvReaderBuilder trimValues() {
        trimValues = true;
        return this;
    }

    public CsvReaderBuilder treatEmptyAsNull() {
        treatEmptyAsNull = true;
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
    
    CsvReaderBuilder usingParser(Function<CsvReaderBuilder, Function<String, Row>> parserFactory) {
        this.parserFactory = parserFactory;
        return this;
    }

    public Stream<Row> build() {
        Stream<String> lines = in;
        Stream<Row> csv = lines.map(parserFactory.apply(CsvReaderBuilder.this)).filter(fields -> fields != null);
        if (skipComments) {
            csv = csv.filter(row -> !row.isComment());
        }
        if (trimValues) {
            csv = csv.map(CsvReaderBuilder::trimValues);
        }
        if (treatEmptyAsNull) {
            csv = csv.map(CsvReaderBuilder::replaceEmptyAsNull);
        }
        return csv;
    }

    private Function<String, Row> createParser() {
        return new RowParser(separators, quote, commentStart, laxMode);
    }

    private static Row trimValues(Row values) {
        if (values.isComment()) {
            return values;
        }
        return new Columns(StreamSupport.stream(values.spliterator(), false)
            .map(column -> column == null ? null : column.trim()).collect(toList()));
    }

    private static Row replaceEmptyAsNull(Row values) {
        if (values.isComment()) {
            return values;
        }
        return new Columns(StreamSupport.stream(values.spliterator(), false)
            .map(column -> (column == null || column.length() == 0) ? null : column).collect(toList()));
    }
}
