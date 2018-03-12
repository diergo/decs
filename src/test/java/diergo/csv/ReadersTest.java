package diergo.csv;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReadersTest {

    @Test
    public void bufferedReaderAsLines() {
        Stream<String> lines = Readers.asLines(new BufferedReader(new StringReader("one\ntwo")));
        assertThat(lines.collect(toList()), hasItems("one", "two"));
    }

    @Test
    public void otherReaderAsLines() {
        Stream<String> lines = Readers.asLines(new StringReader("one\ntwo"));
        assertThat(lines.collect(toList()), hasItems("one", "two"));
    }

    @Test
    public void onCloseClosesReaderWhenRun() throws IOException {
        Reader in = mock(Reader.class);
        Readers.closeHandler(in).run();
        verify(in).close();
    }

    @Test
    public void onCloseHidesExceptionWhenRun() throws IOException {
        Reader in = mock(Reader.class);
        Mockito.doThrow(new IOException()).when(in).close();
        assertThrows(IllegalStateException.class, () -> Readers.closeHandler(in).run());
    }
}