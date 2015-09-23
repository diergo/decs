package diergo.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class CsvReaderStream {

    public static final String DEFAULT_SEPARATORS = ",;\t";
    private static final String[] EMPTY_LINE = new String[0];

    public static CsvReaderStream.Builder toCsvStream(Reader in) {
        return new Builder(BufferedReader.class.isInstance(in) ? BufferedReader.class.cast(in) : new BufferedReader(in));
    }

    private final Function<String,Character> determiner;
    private final char quote;
    private final String commentStart;
    private final StringBuffer formerLines = new StringBuffer();
    private Character separator;

    CsvReaderStream(CharSequence separators, char quote, String commentStart) {
        this.determiner = separators.length() == 1 ? line -> separators.charAt(0) : new AutoSeparatorDeterminer(separators);
        this.quote = quote;
        this.commentStart = commentStart;
    }

    String[] parseLine(String line) {
        if (formerLines.length() > 0) {
            line = formerLines.append(line).toString();
            formerLines.delete(0, formerLines.length());
        }
        if (isEmpty(line)) {
            return EMPTY_LINE;
        }
        int i = 0;
        if (line.startsWith(commentStart)) {
            return new String[]{line};
        }
        if (separator == null) {
            separator = determiner.apply(line);
        }
        CharBuffer elem = CharBuffer.allocate(line.length());
        List<String> data = new ArrayList<>();
        boolean quoted = false;
        boolean isQuote = false;
        for (char c : line.toCharArray()) {
            if (c == separator && (!quoted || isQuote)) {
                data.add(getValue(elem));
                isQuote = false;
            } else if (c == quote) {
                if (isQuote) {
                    elem.append(c);
                    isQuote = false;
                } else if (quoted) {
                    isQuote = true;
                } else if (elem.position() == 0) {
                    quoted = true;
                } else {
                    throw new IllegalArgumentException("CSV need quoting when containing quote at " + i + ": " + line);
                }
            } else {
                elem.append(c);
                isQuote = false;
            }
            ++i;
        }
        if (quoted && !isQuote) {
            formerLines.append(line);
            formerLines.append('\n');
            return null;
        }
        data.add(getValue(elem));
        return data.toArray(new String[data.size()]);
    }

    private boolean isEmpty(String line) {
        return line == null || line.trim().length() == 0;
    }

    private String getValue(CharBuffer value) {
        int length = value.position();
        value.rewind();
        value.limit(length);
        try {
            return value.toString();
        } finally {
            value.clear();
        }
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

    public static class Builder {
        private final BufferedReader in;
        private CharSequence separators = DEFAULT_SEPARATORS;
        private char quote = '"';
        private String commentStart = "#";
        private boolean skipComments;
        private boolean trimFields;
        private boolean treatEmptyAsNull;

        public Builder(BufferedReader in) {
            this.in = in;
        }

        public Builder quotedWith(char quote) {
            this.quote = quote;
            return this;
        }

        public Builder commentsStartWith(String commentStart) {
            this.commentStart = commentStart;
            return this;
        }

        public Builder skipComments() {
            skipComments = true;
            return this;
        }

        public Builder trimFields() {
            trimFields = true;
            return this;
        }

        public Builder treatEmptyAsNull() {
            treatEmptyAsNull = true;
            return this;
        }

        public Builder separatedBy(char separator) {
            this.separators = String.valueOf(separator);
            return this;
        }

        public Builder separatedByAnyOf(CharSequence possibleSeparators) {
            this.separators = possibleSeparators;
            return this;
        }

        public Stream<String[]> stream() {
            Stream<String> lines = in.lines();
            if (skipComments) {
                lines = lines.filter(line -> !line.startsWith("#"));
            }
            Stream<String[]> csv = lines.map(new CsvReaderStream(separators, quote, commentStart)::parseLine).filter(fields -> fields != null);
            if (trimFields) {
                csv = csv.map(CsvReaderStream::trimElements);
            }
            if (treatEmptyAsNull) {
                csv = csv.map(CsvReaderStream::replaceEmptyAsNull);
            }
            return csv;
        }
    }
    
    private class AutoSeparatorDeterminer implements Function<String,Character> {
        private final CharSequence possibleSeparators;
        private Character separator = null;

        private AutoSeparatorDeterminer(CharSequence possibleSeparators) {
            this.possibleSeparators = possibleSeparators;
        }

        @Override
        public Character apply(String line) {
            if (separator != null) {
                return separator;
            }
            if (line == null || line.trim().length() == 0) {
                throw new IllegalStateException("Separator not determined");
            }
            separator = getBestVotedSeparator(voteForSeparators(line));
            return separator;
        }

        private Map<Character, Integer> voteForSeparators(String line) {
            Map<Character, Integer> votes = new HashMap<>();
            for (char c : possibleSeparators.toString().toCharArray()) {
                try {
                    votes.put(c, getFieldCountFromLineParsed(line, c));
                } catch (IllegalArgumentException e) {
                    votes.put(c, 0);
                }
            }
            return votes;
        }

        private char getBestVotedSeparator(Map<Character, Integer> votes) {
            return votes.entrySet().stream()
                .reduce((e1, e2) -> e1.getValue() < e2.getValue() ? e2 : e1)
                .get().getKey();
        }

        private int getFieldCountFromLineParsed(String line, char separator) {
            return CsvReaderStream.this.parseLine(line).length;
        }
    }
}
