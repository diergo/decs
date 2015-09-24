package diergo.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Stream;

public class CsvReaderBuilder implements Iterable<String[]> {

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
    private CharSequence separators = DEFAULT_SEPARATORS;
    private char quote = DEFAULT_QUOTE;
    private String commentStart = DEFAULT_COMMENT_START;
    private boolean skipComments;
    private boolean trimFields;
    private boolean treatEmptyAsNull;

    private CsvReaderBuilder(Stream<String> in) {
        this.in = in;
    }

    public CsvReaderBuilder quotedWith(char quote) {
        this.quote = quote;
        return this;
    }

    public CsvReaderBuilder commentsStartWith(String commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    public CsvReaderBuilder skipComments() {
        skipComments = true;
        return this;
    }

    public CsvReaderBuilder trimFields() {
        trimFields = true;
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

    @Override
    public Iterator<String[]> iterator() {
        return Spliterators.iterator(build().spliterator());
    }

    public Stream<String[]> build() {
        Stream<String> lines = in;
        if (skipComments) {
            lines = lines.filter(line -> !line.startsWith(commentStart));
        }
        Stream<String[]> csv = lines.map(new CsvLineParser(separators, quote, commentStart)::apply).filter(fields -> fields != null);
        if (trimFields) {
            csv = csv.map(CsvReaderBuilder::trimElements);
        }
        if (treatEmptyAsNull) {
            csv = csv.map(CsvReaderBuilder::replaceEmptyAsNull);
        }
        return csv;
    }

    private static String[] trimElements(String[] fields) {
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = fields[i].trim();
        }
        return fields;
    }

    private static String[] replaceEmptyAsNull(String[] fields) {
        for (int i = 0; i < fields.length; ++i) {
            if (fields[i].length() == 0) {
                fields[i] = null;
            }
        }
        return fields;
    }
}
