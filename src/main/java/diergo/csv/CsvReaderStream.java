package diergo.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static diergo.csv.Option.*;

public class CsvReaderStream {

    public static final String DEFAULT_SEPARATORS = ",;\t";
    private static final Logger LOG = LoggerFactory.getLogger(CsvReaderStream.class);
    private static final String[] EMPTY_LINE = new String[0];

    public static CsvReaderStream.Builder toCsvStream(Reader in) {
        return new Builder(BufferedReader.class.isInstance(in) ? BufferedReader.class.cast(in) : new BufferedReader(in));
    }

    private final Set<Option> options;
    private final SeparatorDeterminer determiner;
    private final StringBuffer formerLines = new StringBuffer();
    private Character separator;

    CsvReaderStream(Set<Option> options, SeparatorDeterminer determiner) {
        this.options = options;
        this.determiner = determiner;
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
        if (line.startsWith("#")) {
            if (options.contains(COMMENTS_SKIPPED)) {
                LOG.debug("Skipped comment '{}'", line);
                return null;
            } else {
                return new String[]{line};
            }
        }
        if (separator == null) {
            separator = determiner.determineSeparator(line);
        }
        CharBuffer elem = CharBuffer.allocate(line.length());
        List<String> data = new ArrayList<>();
        boolean quoted = false;
        boolean isQuote = false;
        for (char c : line.toCharArray()) {
            if (c == separator && (!quoted || isQuote)) {
                data.add(getValue(elem));
                isQuote = false;
            } else if (c == QUOTE) {
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
            String result = value.toString();
            if (options.contains(TRIM)) {
                result = result.trim();
            }
            if (options.contains(EMPTY_AS_NULL) && result.length() == 0) {
                return null;
            }
            return result;
        } finally {
            value.clear();
        }
    }

    public static class Builder {
        private final BufferedReader in;
        private final Set<Option> options = EnumSet.noneOf(Option.class);
        private SeparatorDeterminer determiner = new AutoSeparatorDeterminer(DEFAULT_SEPARATORS);

        public Builder(BufferedReader in) {
            this.in = in;
        }

        public Builder separatedBy(char separator) {
            this.determiner = new FixedSeparatorDeterminer(separator);
            return this;
        }

        public Builder separatedByAnyOf(CharSequence possibleSeparators) {
            this.determiner = new AutoSeparatorDeterminer(possibleSeparators);
            return this;
        }

        public Builder withOption(Option option) {
            this.options.add(option);
            return this;
        }

        public Builder withoutOption(Option option) {
            this.options.remove(option);
            return this;
        }

        public Stream<String[]> build() {
            return in.lines().map(new CsvReaderStream(options, determiner)::parseLine).filter(fields -> fields != null);
        }
    }
}
