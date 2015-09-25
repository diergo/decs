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

public class CsvWriterCollector implements Collector<String, Writer, Void> {
    
    public static Collector<String, Writer, Void> toWriter(Writer out) {
        return new CsvWriterCollector(out);
    }

    private static final Logger LOG = LoggerFactory.getLogger(CsvWriterCollector.class);
    
    private final Writer out;

    private CsvWriterCollector(Writer out) {
        this.out = out;
    }

    @Override
    public Supplier<Writer> supplier() {
        return () -> out;
    }

    @Override
    public BiConsumer<Writer, String> accumulator() {
        return (out, line) -> appendLine(line, out);
    }

    @Override
    public BinaryOperator<Writer> combiner() {
        return (o1, o2) -> o1;
    }

    @Override
    public Function<Writer, Void> finisher() {
        return (o) -> null;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    private void appendLine(String line, Writer out) {
        try {
            out.append(line);
            out.append('\n');
        } catch (IOException e) {
            LOG.error("Cannot write line '{}'", line, e);
        }
    }
}
