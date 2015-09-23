package diergo.csv;

import org.junit.Test;

import java.io.StringReader;
import java.util.stream.Stream;

import static diergo.csv.CsvReaderStream.toCsvStream;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CsvReaderStreamTest {

    @Test
    public void anEmptyReaderCreatesAnEmptyStream() {
        StringReader csv = new StringReader("");
        Stream<String[]> rows = toCsvStream(csv).stream();
        assertThat(rows.count(), is(0L));
    }

    @Test
    public void commentsAreReadAsSingleField() {
        StringReader csv = new StringReader("#comment;no fields");
        Stream<String[]> rows = toCsvStream(csv)
            .separatedBy(';').commentsStartWith("#").stream();
        assertThat(rows.findAny().get(), is(new String[] {"#comment;no fields"}));
    }

    @Test
    public void commentsAreSkippedOptionally() {
        StringReader csv = new StringReader("#comment;no fields");
        Stream<String[]> rows = toCsvStream(csv)
            .separatedBy(';').commentsStartWith("#").skipComments().stream();
        assertThat(rows.count(), is(0L));
    }

}
