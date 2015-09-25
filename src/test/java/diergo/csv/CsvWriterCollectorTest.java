package diergo.csv;

import org.junit.Test;

import java.io.StringWriter;
import java.util.stream.Stream;

import static diergo.csv.CsvWriterCollectorBuilder.collectToCsvWriter;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvWriterCollectorTest {

    @Test
    public void anEmptyStreamCreatesAnEmptyWriter() {
        StringWriter out = new StringWriter();
        Stream.<Row>empty().collect(collectToCsvWriter(out).build());

        assertThat(out.getBuffer().toString(), is(""));
    }
}
