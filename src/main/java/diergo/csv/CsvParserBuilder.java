package diergo.csv;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static diergo.csv.Row.DEFAULT_QUOTE;

public class CsvParserBuilder {

    public static final String DEFAULT_SEPARATORS = ",;\t";

    public static CsvParserBuilder csvParser() {
        return new CsvParserBuilder();
    }

    private CharSequence separators = DEFAULT_SEPARATORS;
    private char quote = DEFAULT_QUOTE;
    private String commentStart = null;
    private boolean laxMode = false;
    private BiFunction<RuntimeException, String, List<Row>> errorHandler;

    public CsvParserBuilder quotedWith(char quote) {
        this.quote = quote;
        return this;
    }

    public CsvParserBuilder commentsStartWith(String commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    public CsvParserBuilder inLaxMode() {
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

    public CsvParserBuilder handlingErrors(BiFunction<RuntimeException, String, List<Row>> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public Function<String, List<Row>> build() {
        BiFunction<RuntimeException, String, List<Row>> effectiveErrorHandler = errorHandler;
        if (effectiveErrorHandler == null) {
            effectiveErrorHandler = commentStart == null ? new LoggingCsvParserErrorHandler() : new CommentingCsvParserErrorHandler();
        }
        return new RowParser(separators, quote, commentStart, laxMode, effectiveErrorHandler);
    }
}
