package diergo.csv;

import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static diergo.csv.Readers.asLines;
import static diergo.csv.Row.DEFAULT_QUOTE;

public class CsvParserBuilder {

    public static final String DEFAULT_SEPARATORS = ",;\t";

    public static CsvParserBuilder buildCsvParser(Stream<String> lines) {
        return new CsvParserBuilder(lines);
    }

    public static CsvParserBuilder buildCsvParser(Reader in) {
        return buildCsvParser(asLines(in));
    }

    private final Stream<String> in;
    private Function<CsvParserBuilder, Function<String, List<Row>>> parserFactory;
    private CharSequence separators = DEFAULT_SEPARATORS;
    private char quote = DEFAULT_QUOTE;
    private String commentStart = null;
    private boolean laxMode = false;

    private CsvParserBuilder(Stream<String> in) {
        this.in = in;
        parserFactory = CsvParserBuilder::createParser;
    }

    public CsvParserBuilder quotedWith(char quote) {
        this.quote = quote;
        return this;
    }

    public CsvParserBuilder commentsStartWith(String commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    public CsvParserBuilder laxMode() {
        this.laxMode = true;
        return this;
    }

    public CsvParserBuilder separatedBy(char separator) {
        this.separators = String.valueOf(separator);
        return this;
    }

    public CsvParserBuilder separatedByAnyOf(CharSequence possibleSeparators) {
        this.separators = possibleSeparators;
        return this;
    }

    CsvParserBuilder creatingParser(Function<CsvParserBuilder, Function<String, List<Row>>> parserFactory) {
        this.parserFactory = parserFactory;
        return this;
    }

    public Stream<Row> build() {
        return in.map(parserFactory.apply(this)).flatMap(Collection::stream);
    }

    private Function<String, List<Row>> createParser() {
        return new RowParser(separators, quote, commentStart, laxMode);
    }
}
