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

public class CsvWriterCollector<O extends Writer> implements Collector<String, O, O> {
    
    public static <O extends Writer> Collector<String, O, O> toWriter(O out) {
        return new CsvWriterCollector<>(out);
    }

    private static final Logger LOG = LoggerFactory.getLogger(CsvWriterCollector.class);
    
    private final O out;

    private CsvWriterCollector(O out) {
        this.out = out;
    }

    @Override
    public Supplier<O> supplier() {
        return () -> out;
    }

    @Override
    public BiConsumer<O, String> accumulator() {
        return (out, line) -> appendLine(line, out);
    }

    @Override
    public BinaryOperator<O> combiner() {
        return (o1, o2) -> o1;
    }

    @Override
    public Function<O, O> finisher() {
        return (o) -> out;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.noneOf(Characteristics.class);
    }

    private void appendLine(String line, O out) {
        try {
            out.append(line);
            out.append('\n');
        } catch (IOException e) {
            LOG.error("Cannot write line '{}'", line, e);
        }
    }
}
