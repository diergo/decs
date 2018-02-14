package diergo.csv;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.EnumSet.noneOf;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.UNORDERED;

/**
 * Helpers for {@link Appendable} usage.
 *
 * @since 3.1.1
 */
public class Appendables {

    /**
     * Used as a default line separator
     */
    public static final String CRLF = "\r\n";

    /**
     * Creates a collector writing lines to a specific appendable using {@link #CRLF} as line separator.
     *
     * @see #toAppendable(Appendable, char)
     */
    public static <R extends Appendable> Collector<String, Appendable, R> toAppendable(R out) {
        return new CsvAppendableCollector<>(new StringAppendable<>(out, CRLF), true);
    }

    /**
     * Creates a collector writing lines to a specific appendable.
     * Typical usage for a stream of strings:
     *
     * <br/>{@link Stream Stream}{@code <String>} lines = ...;
     * <br/>{@code lines.}{@link Stream#collect(Collector) collect}({@code toWriter(out)})
     *
     * @param <R> the result type of the reduction operation, any {@link Appendable}
     */
    public static <R extends Appendable> Collector<String, Appendable, R> toAppendable(R out, char lineSep) {
        return new CsvAppendableCollector<>(new StringAppendable<>(out, String.valueOf(lineSep)), true);
    }

    /**
     * Creates a collector writing lines to a specific appendable using {@link #CRLF} as line separator.
     *
     * @see #toAppendableUnordered(Appendable, char)
     */
    public static <R extends Appendable> Collector<String, Appendable, R> toAppendableUnordered(R out) {
        return new CsvAppendableCollector<>(new StringAppendable<>(out, CRLF), false);
    }

    /**
     * Creates a collector writing lines to a specific appendable.
     * The lines in the resulting appendable may have any order and by written concurrently.
     * Do not use this when a header line with column names is included in the stream!
     * Typical usage for a stream of strings:
     *
     * <br/>{@link Stream Stream}{@code <String>} lines = ...;
     * <br/>{@code lines.}{@link Stream#collect(Collector) collect}({@code toWriterUnordered(out)})
     *
     * @param <R> the result type of the reduction operation, any {@link Appendable}
     * @see Stream#parallel()
     */
    public static <R extends Appendable> Collector<String, Appendable, R> toAppendableUnordered(R out, char lineSep) {
        return new CsvAppendableCollector<>(new StringAppendable<>(out, String.valueOf(lineSep)), false);
    }

    /**
     * Creates a consumer writing lines to a specific appendable.
     * Typical usage for a stream of strings:
     *
     * <br/>{@link Stream Stream}{@code <String>} lines = ...;
     * <br/>{@code lines.}{@link Stream#forEach(Consumer) forEach}({@code consumeTo(out)})
     * or {@code lines.}{@link Stream#forEachOrdered(Consumer) forEachOrdered}({@code consumeTo(out)})
     */
    public static Consumer<String> consumeTo(Appendable out) {
        return new StringAppendable<>(out, CRLF);
    }

    private static class StringAppendable<O extends Appendable> implements Consumer<String> {
        private final O out;
        private final String lineSep;

        protected StringAppendable(O out, String lineSep) {
            this.out = out;
            this.lineSep = lineSep;
        }

        @Override
        public void accept(String line) {
            try {
                // done with one append call to be thread safe!
                out.append(line + lineSep);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static class CsvAppendableCollector<R extends Appendable> implements Collector<String, Appendable, R> {

        private final StringAppendable<R> consumer;
        private final boolean ordered;

        CsvAppendableCollector(StringAppendable<R> consumer, boolean ordered) {
            this.consumer = consumer;
            this.ordered = ordered;
        }

        @Override
        public Supplier<Appendable> supplier() {
            return () -> consumer.out;
        }
    
        @Override
        public BiConsumer<Appendable, String> accumulator() {
            return (out, line) -> consumer.accept(line);
        }
    
        @Override
        public BinaryOperator<Appendable> combiner() {
            return (o1, o2) -> o1;
        }
    
        @Override
        public Function<Appendable, R> finisher() {
            return o -> consumer.out;
        }
    
        @Override
        public Set<Characteristics> characteristics() {
            return ordered ? noneOf(Characteristics.class) : EnumSet.of(UNORDERED, CONCURRENT);
        }
    }
}
