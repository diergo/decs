package diergo.csv;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.stream.Stream;

public class Readers {

    public static Stream<String> asLines(Reader in) {
        BufferedReader reader = BufferedReader.class.isInstance(in) ? BufferedReader.class.cast(in) : new BufferedReader(in);
        return reader.lines();
    }

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
