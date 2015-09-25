package diergo.csv;

import org.junit.Test;

import java.io.StringWriter;
import java.util.Collections;

import static diergo.csv.CsvWriterCollector.toWriter;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvWriterCollectorTest {

    @Test
    public void emptyStreamCreatedEmptyWriter() {
        StringWriter out = new StringWriter();
        Collections.<String>emptyList().stream().collect(toWriter(out));
        
        assertThat(out.toString(), is(""));
    }

    @Test
    public void eachStringBecomesALine() {
        StringWriter out = new StringWriter();
        asList("one", "two").stream().collect(toWriter(out));

        assertThat(out.toString(), is("one\ntwo\n"));
    }
}
