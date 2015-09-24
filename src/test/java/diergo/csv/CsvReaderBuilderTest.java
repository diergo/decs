package diergo.csv;

import org.junit.Test;

import java.io.StringReader;
import java.util.stream.Stream;

import static diergo.csv.CsvReaderBuilder.toCsvStream;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CsvReaderBuilderTest {

    @Test
    public void anEmptyReaderCreatesAnEmptyStream() {
        StringReader csv = new StringReader("");
        Stream<String[]> rows = toCsvStream(csv).build();
        assertThat(rows.count(), is(0L));
    }

    @Test
    public void commentsAreSkippedOptionally() {
        StringReader csv = new StringReader("#comment;no columns");
        Stream<String[]> rows = toCsvStream(csv)
            .separatedBy(';').commentsStartWith("#").skipComments().build();
        assertThat(rows.count(), is(0L));
    }

}
