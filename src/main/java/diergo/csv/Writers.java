package diergo.csv;

import java.io.Writer;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static diergo.csv.Appendables.toAppendable;
import static diergo.csv.Appendables.toAppendableUnordered;

/**
 * Helpers for {@link Writer} usage.
 *
 * @see Appendables
 */
@Deprecated
public class Writers {

    /**
     * Creates a collector writing lines to a specific writer using {@link Appendables#CRLF} as line separator.
     *
     * @see #toWriter(Writer, char)
     */
    @Deprecated
    public static <R extends Writer> Collector<String, Appendable, R> toWriter(R out) {
        return toAppendable(out);
    }

    /**
     * Creates a collector writing lines to a specific writer.
     * Typical usage for a stream of strings:
     * <p>
     * <br/>{@link java.util.stream.Stream Stream}{@code <String>} lines = ...;
     * <br/>{@code lines.}{@link java.util.stream.Stream#collect(Collector) collect}({@code toWriter(out)})
     *
     * @param <R> the result type of the reduction operation, any {@link Writer}
     * @since 3.1.0
     */
    @Deprecated
    public static <R extends Writer> Collector<String, Appendable, R> toWriter(R out, char lineSep) {
        return toAppendable(out, lineSep);
    }

    /**
     * Creates a collector writing lines to a specific writer using {@link Appendables#CRLF} as line separator.
     *
     * @see #toWriterUnordered(Writer, char)
     */
    @Deprecated
    public static <R extends Writer> Collector<String, Appendable, R> toWriterUnordered(R out) {
        return toAppendableUnordered(out);
    }

    /**
     * Creates a collector writing lines to a specific writer.
     * The lines in the resulting writer may have any order and by written concurrently.
     * Do not use this when a header line with column names is included in the stream!
     * Typical usage for a stream of strings:
     * <p>
     * <br/>{@link java.util.stream.Stream Stream}{@code <String>} lines = ...;
     * <br/>{@code lines.}{@link java.util.stream.Stream#collect(Collector) collect}({@code toWriterUnordered(out)})
     *
     * @param <R> the result type of the reduction operation, any {@link Writer}
     * @see Stream#parallel()
     * @since 3.1.0
     */
    @Deprecated
    public static <R extends Writer> Collector<String, Appendable, R> toWriterUnordered(R out, char lineSep) {
        return toAppendableUnordered(out, lineSep);
    }
}
