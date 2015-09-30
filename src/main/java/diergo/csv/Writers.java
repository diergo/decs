package diergo.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.EnumSet.noneOf;

/**
 * Helpers for {@link Writer} usage.
 */
public class Writers {

    private static final Logger LOG = LoggerFactory.getLogger(CsvWriterCollector.class);

    /**
     * Creates a collector writing lines to a specific writer. Typical usage for a stream of strings:
     *
     * <br/>{@link java.util.stream.Stream Stream}{@code <String>} lines = ...;
     * <br/>{@code lines.}{@link java.util.stream.Stream#collect(Collector) collect}({@code toWriter(out)})
     *
     * @param <R> the result type of the reduction operation, any {@link Writer}
     */
    public static <R extends Writer> Collector<String, Appendable, R> toWriter(R out) {
        return new CsvWriterCollector<>(out);
    }

    /**
     * Creates a consumer writing lines to a specific writer. Typical usage for a stream of strings:
     *
     * <br/>{@link java.util.stream.Stream Stream}{@code <String>} lines = ...;
     * <br/>{@code lines.}{@link java.util.stream.Stream#forEach(Consumer) forEach}({@code consumeTo(out)})
     * or {@code lines.}{@link java.util.stream.Stream#forEachOrdered(Consumer) forEachOrdered}({@code consumeTo(out)})
     */
    public static Consumer<String> consumeTo(Writer out) {
        return line -> appendLine(out, line);
    }

    private static void appendLine(Appendable out, String line) {
        try {
            out.append(line);
            out.append('\n');
        } catch (IOException e) {
            LOG.error("Cannot write line '{}'", line, e);
        }
    }

    private static class CsvWriterCollector<R extends Writer> implements Collector<String, Appendable, R> {
    
        private final R out;
    
        CsvWriterCollector(R out) {
            this.out = out;
        }
    
        @Override
        public Supplier<Appendable> supplier() {
            return () -> out;
        }
    
        @Override
        public BiConsumer<Appendable, String> accumulator() {
            return Writers::appendLine;
        }
    
        @Override
        public BinaryOperator<Appendable> combiner() {
            return (o1, o2) -> o1;
        }
    
        @Override
        public Function<Appendable, R> finisher() {
            return (o) -> out;
        }
    
        @Override
        public Set<Characteristics> characteristics() {
            return noneOf(Characteristics.class);
        }
    }
}
