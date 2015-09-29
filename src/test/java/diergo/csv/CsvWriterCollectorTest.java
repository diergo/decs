package diergo.csv;

import org.junit.Test;

import java.io.StringWriter;
import java.util.stream.Stream;

import static diergo.csv.CsvWriterCollector.toWriter;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvWriterCollectorTest {

    @Test
    public void emptyStreamCreatedEmptyWriter() {
        StringWriter out = Stream.<String>empty().collect(toWriter(new StringWriter()));

        assertThat(out.toString(), is(""));
    }

    @Test
    public void eachStringBecomesALine() {
        StringWriter out = Stream.of("one", "two").collect(toWriter(new StringWriter()));

        assertThat(out.toString(), is("one\ntwo\n"));
    }
}
