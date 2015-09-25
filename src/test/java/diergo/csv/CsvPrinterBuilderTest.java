package diergo.csv;

import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvPrinterBuilderTest {

    @Test
    public void commentIsPrintedPrefixed() {
        Function<Row, String> printer = CsvPrinterBuilder.buildCsvPrinter().build();
        String line = printer.apply(new Comment("comment"));
        assertThat(line, is("#comment"));
    }
}
