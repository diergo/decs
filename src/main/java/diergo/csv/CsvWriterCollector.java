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

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

class CsvWriterCollector implements Collector<Row, Writer, Writer> {

    private static final Logger LOG = LoggerFactory.getLogger(CsvWriterCollector.class);

    private final Writer out;
    private final Function<Row, String> printer;

    CsvWriterCollector(Writer out, Function<Row, String> printer) {
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
}
