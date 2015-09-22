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
        Stream<String[]> csv = toCsvStream(new StringReader("")).build();
        assertThat(csv.count(), is(0L));
    }

}