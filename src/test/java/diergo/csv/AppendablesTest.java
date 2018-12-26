package diergo.csv;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.stream.Stream;

import static diergo.csv.Appendables.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

class AppendablesTest {

    @Test
    void emptyStreamCollectsToAnUnchangedWriter() {
        StringWriter out = Stream.<String>empty().collect(toAppendable(new StringWriter()));

        assertThat(out.toString(), is(""));
    }

    @Test
    void eachStringIsCollectedToALine() {
        StringWriter out = Stream.of("one", "two").collect(toAppendable(new StringWriter()));

        assertThat(out.toString(), is("one\r\ntwo\r\n"));
    }

    @Test
    void lineSeparatorCanBeConfigured() {
        StringWriter out = Stream.of("one", "two").collect(toAppendable(new StringWriter(), '\n'));

        assertThat(out.toString(), is("one\ntwo\n"));
    }

    @Test
    void eachStringIsCollectedUnordered() {
        String[] lines = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
        StringWriter out = Stream.of(lines)
                .parallel().collect(toAppendableUnordered(new StringWriter()));

        String content = out.toString();
        for (String line : lines) {
            assertThat(content, containsString(line + "\r\n"));
        }
    }

    @Test
    void eachStringIsConsumedToALine() {
        StringWriter out = new StringWriter();
        Stream.of("one", "two").forEach(consumeTo(out));

        assertThat(out.toString(), is("one\r\ntwo\r\n"));
    }

    @Test
    void ioExcpetionIsWrappedToUnchecked() throws IOException {
        Writer out = Mockito.mock(Writer.class);
        when(out.append(anyString()))
                .thenThrow(new IOException("test"));

        assertThrows(UncheckedIOException.class, () -> consumeTo(out).accept("line"));
    }
}
