package diergo.csv;

import org.junit.Test;

import java.io.StringWriter;
import java.util.stream.Stream;

import static diergo.csv.CsvWriterCollector.toCsvWriter;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvWriterCollectorTest {

    @Test
    public void anEmptyStreamCreatesAnEmptyWriter() {
        StringWriter out = new StringWriter();
        Stream.<String[]>empty().collect(toCsvWriter(out).build());

        assertThat(out.getBuffer().toString(), is(""));
    }

    @Test
    public void headerIsWritten() {
        StringWriter out = new StringWriter();
        Stream.<String[]>empty().collect(toCsvWriter(out).withHeader("left", "middle", "right").build());

        assertThat(out.getBuffer().toString(), is("left,middle,right\n"));
    }
}