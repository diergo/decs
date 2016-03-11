package diergo.csv;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.stream.Stream;

import static diergo.csv.Appendables.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class AppendablesTest {

    @Test
    public void emptyStreamCollectsToAnUnchangedWriter() {
        StringWriter out = Stream.<String>empty().collect(toAppendable(new StringWriter()));

        assertThat(out.toString(), is(""));
    }

    @Test
    public void eachStringIsCollectedToALine() {
        StringWriter out = Stream.of("one", "two").collect(toAppendable(new StringWriter()));

        assertThat(out.toString(), is("one\r\ntwo\r\n"));
    }

    @Test
    public void lineSeparatorCanBeConfigured() {
        StringWriter out = Stream.of("one", "two").collect(toAppendable(new StringWriter(), '\n'));

        assertThat(out.toString(), is("one\ntwo\n"));
    }

    @Test
    public void eachStringIsCollectedUnordered() {
        String[] lines = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
        StringWriter out = Stream.of(lines)
            .parallel().collect(toAppendableUnordered(new StringWriter()));

        String content = out.toString();
        for (String line : lines) {
            assertThat(content, containsString(line + "\r\n"));
        }
    }

    @Test
    public void eachStringIsConsumedToALine() {
        StringWriter out = new StringWriter();
        Stream.of("one", "two").forEach(consumeTo(out));

        assertThat(out.toString(), is("one\r\ntwo\r\n"));
    }
    
    @Test(expected = UncheckedIOException.class)
    public void ioExcpetionIsWrappedToUnchecked() throws IOException {
        Writer out = Mockito.mock(Writer.class);
        when(out.append(anyString()))
            .thenThrow(new IOException("test"));
        consumeTo(out).accept("line");
    }
}
