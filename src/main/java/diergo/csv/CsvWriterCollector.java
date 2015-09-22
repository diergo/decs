package diergo.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;

import static diergo.csv.Option.*;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public class CsvWriterCollector implements Collector<String[], Writer, Writer> {

    private static final Logger LOG = LoggerFactory.getLogger(CsvWriterCollector.class);

    private static final Pattern QUOTE_PATTERN = Pattern.compile(String.valueOf(QUOTE));
    private static final String QUOTE_REPLACEMENT = new String(new char[]{QUOTE, QUOTE});

    public static CsvWriterCollector.Builder toCsvWriter(Writer out) {
        return new Builder(out);
    }

    private final Writer out;
    private final char separator;
    private final String[] header;
    private final Set<Option> options;

    private CsvWriterCollector(Writer out, char separator, String[] header, Set<Option> options) {
        this.out = out;
        this.separator = separator;
        this.header = header;
        this.options = options;
    }


    @Override
    public Supplier<Writer> supplier() {
        if (header != null) {
            appendLine(header, out);
        }
        return () -> out;
    }

    @Override
    public BiConsumer<Writer, String[]> accumulator() {
        return (out, line) -> appendLine(line, out);
    }

    @Override
    public BinaryOperator<Writer> combiner() {
        return (o1, o2) -> o1;
    }

    @Override
    public Function<Writer, Writer> finisher() {
        return (o) -> o;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(IDENTITY_FINISH);
    }

    private void appendLine(String[] fields, Writer out) {
        if (fields.length == 1 && fields[0].startsWith("#") && options.contains(COMMENTS_SKIPPED)) {
            LOG.debug("Skipped comment '{}'", fields[0]);
            return;
        }
        String line = createLine(fields);
        try {
            out.append(line);
            out.append('\n');
        } catch (IOException e) {
            LOG.error("Cannot write line '{}'", line, e);
        }
    }

    private String createLine(String[] fields) {
        StringBuilder line = new StringBuilder();
        boolean first = true;
        for (String field : fields) {
            if (!first) {
                line.append(separator);
            }
            line.append(createField(field));
            first = false;
        }
        return line.toString();
    }

    private String createField(String field) {
        if (options.contains(TRIM)) {
            field = field.trim();
        }
        String elem = field;
        if (elem == null) {
            return options.contains(EMPTY_AS_NULL) ? "" : "null";
        }
        boolean containsQuote = elem.indexOf(QUOTE) != -1;
        boolean containsNewline = elem.indexOf('\n') != -1 || elem.indexOf('\r') != -1;
        boolean quote = elem.indexOf(separator) != -1 || containsQuote || containsNewline;
        if (quote) {
            if (containsQuote) {
                elem = QUOTE_PATTERN.matcher(elem).replaceAll(QUOTE_REPLACEMENT);
            }
            elem = QUOTE + elem + QUOTE;
        }
        return elem;
    }

    public static class Builder {
        private final Writer out;
        private final Set<Option> options = EnumSet.noneOf(Option.class);
        private char separator = ',';
        private String[] header;

        public Builder(Writer out) {
            this.out = out;
        }

        public Builder separatedBy(char separator) {
            this.separator = separator;
            return this;
        }

        public Builder withHeader(String... header) {
            this.header = header;
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

        public Collector<String[], Writer, Writer> build() {
            return new CsvWriterCollector(out, separator, header, options);
        }
    }
}
