package diergo.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.EnumSet.noneOf;

/**
 * Collect lines to a specific writer.
 *
 * lines.{@link java.util.stream.Stream#collect(Collector) collect}({@link #toWriter(Writer) toWriter(out)})
 *
 * @param <R> the result type of the reduction operation, any {@link Writer}
 */
public class CsvWriterCollector<R extends Writer> implements Collector<String, R, R> {

    /**
     * Creates a new collector using the writer and returning it
     */
    public static <R extends Writer> Collector<String, R, R> toWriter(R out) {
        return new CsvWriterCollector<>(out);
    }

    private static final Logger LOG = LoggerFactory.getLogger(CsvWriterCollector.class);
    
    private final R out;

    private CsvWriterCollector(R out) {
        this.out = out;
    }

    /**
     * Returns a supplier offering the original writer.
     * @see #toWriter(Writer)
     */
    @Override
    public Supplier<R> supplier() {
        return () -> out;
    }

    /**
     * Returns a consumer appending the line and a newline to the writer.
     */
    @Override
    public BiConsumer<R, String> accumulator() {
        return (out, line) -> appendLine(line, out);
    }

    @Override
    public BinaryOperator<R> combiner() {
        return (o1, o2) -> o1;
    }

    @Override
    public Function<R, R> finisher() {
        return (o) -> out;
    }

    /**
     * As the writer is ordered there are no characteristics.
     */
    @Override
    public Set<Characteristics> characteristics() {
        return noneOf(Characteristics.class);
    }

    private void appendLine(String line, R out) {
        try {
            out.append(line);
            out.append('\n');
        } catch (IOException e) {
            LOG.error("Cannot write line '{}'", line, e);
        }
    }
}
