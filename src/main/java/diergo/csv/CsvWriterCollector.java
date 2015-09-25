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
import java.util.stream.Collector;

import static diergo.csv.Row.DEFAULT_COMMENT_START;
import static diergo.csv.Row.DEFAULT_QUOTE;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public class CsvWriterCollector implements Collector<Row, Writer, Writer> {

    private static final Logger LOG = LoggerFactory.getLogger(CsvWriterCollector.class);

    public static CsvWriterCollector.Builder toCsvWriter(Writer out) {
        return new Builder(out);
    }

    private final Writer out;
    private final Function<Row, String> printer;

    private CsvWriterCollector(Writer out, Function<Row, String> printer) {
        this.out = out;
        this.printer = printer;
    }

    @Override
    public Supplier<Writer> supplier() {
        return () -> out;
    }

    @Override
    public BiConsumer<Writer, Row> accumulator() {
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

    private void appendLine(Row row, Writer out) {
        try {
            out.append(printer.apply(row));
        } catch (IOException e) {
            LOG.error("Cannot write line '{}'", row, e);
        }
    }


    public static class Builder {
        private final Writer out;
        private char separator = ',';
        private char quote = DEFAULT_QUOTE;
        private String commentStart = DEFAULT_COMMENT_START;

        public Builder(Writer out) {
            this.out = out;
        }

        public Builder separatedBy(char separator) {
            this.separator = separator;
            return this;
        }

        public Builder quotedWith(char quote) {
            this.quote = quote;
            return this;
        }

        public Builder commentsStartWith(String commentStart) {
            this.commentStart = commentStart;
            return this;
        }

        public Collector<Row, Writer, Writer> build() {
            return new CsvWriterCollector(out, new RowPrinter(separator, quote, commentStart));
        }
    }
}
