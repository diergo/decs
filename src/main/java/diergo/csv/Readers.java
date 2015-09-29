package diergo.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.stream.Stream;

/**
 * Helpers for {@link Reader} usage.
 */
public class Readers {

    /**
     * Create a stream of lines from the reader. If needed the reader is wrapped.
     * @see BufferedReader#lines() 
     */
    public static Stream<String> asLines(Reader in) {
        BufferedReader reader = BufferedReader.class.isInstance(in) ? BufferedReader.class.cast(in) : new BufferedReader(in);
        return reader.lines();
    }

    /**
     * Creates a handler to close the reader wrapping any exception to an unchecked one. 
     * @see Stream#onClose(Runnable) 
     */
    public static Runnable closeHandler(Reader in) {
        return () -> {
            try {
                in.close();
            } catch (Exception e) {
                throw new IllegalStateException("Cannot close underlying reader", e);
            }
        };
    }

    private Readers() {
    }
}
