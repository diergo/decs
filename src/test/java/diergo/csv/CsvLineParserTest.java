package diergo.csv;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvLineParserTest {

    @Test
    public void emptyLineIsNoColumns() {
        assertThat(parse(""), is(new String[0]));
    }

    @Test
    public void commentsAreReadAsSingleColumns() {
        assertThat(parse("#comment;no columns"), is(new String[] {"#comment;no columns"}));
    }


    private String[] parse(String line) {
        return parse(";", '"', "#", line);
    }

    private String[] parse(CharSequence separators, char quote, String commentStart, String line) {
        return new CsvLineParser(separators, quote, commentStart).apply(line);
    }
}