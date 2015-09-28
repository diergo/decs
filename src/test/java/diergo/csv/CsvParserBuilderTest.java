package diergo.csv;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static diergo.csv.CsvParserBuilder.buildCsvParser;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CsvParserBuilderTest {

    private Function<String, List<Row>> reader;
    
    @Test
    public void byDefaultALineParserIsCreated() throws ReflectiveOperationException {
        CsvParserBuilder builder = buildCsvParser(new StringReader(""));
        RowParser parser = getLineParser(builder);

        assertThat(parser.quote, is('"'));
        assertThat(parser.commentStart, is("#"));
    }

    @Test(expected = IllegalStateException.class)
    public void byDefaultAnAutoSeparatorDeterminerIsConfiguredWhichCannotHandleAnEmptyLine() throws ReflectiveOperationException {
        CsvParserBuilder builder = buildCsvParser(new StringReader(""));
        RowParser parser = getLineParser(builder);
        parser.determiner.apply("");
    }

    @Test
    public void allConfigurationsArePassedToParser() throws ReflectiveOperationException {
        CsvParserBuilder builder = buildCsvParser(new StringReader("")).commentsStartWith("//").quotedWith('\'').separatedBy(',');
        RowParser parser = getLineParser(builder);

        assertThat(parser.quote, is('\''));
        assertThat(parser.commentStart, is("//"));
        assertThat(parser.determiner.apply(""), is(','));
    }

    @Test
    public void anEmptyReaderCreatesAnEmptyStream() {
        Stream<Row> rows = builderWithMockReader("").build();

        assertThat(rows.count(), is(0L));
        
        verify(reader, never()).apply(anyString());
    }

    @Test
    public void theParserIsCalledForEveryLine() {
        Stream<Row> rows = builderWithMockReader("line;1\nline;2").build();
        when(reader.apply(anyString()))
            .thenAnswer(invocation -> singletonList(new Columns("line", "n")));

        assertThat(rows.count(), is(2L));

        verify(reader).apply(eq("line;1"));
        verify(reader).apply(eq("line;2"));
    }
    
    @Before
    @SuppressWarnings("unchecked")
    public void createReaderMock() {
        reader = mock(Function.class); 
    }
    
    private CsvParserBuilder builderWithMockReader(String csv) {
        return buildCsvParser(new StringReader(csv)).creatingParser(builder -> reader);
    }

    @SuppressWarnings("unchecked")
    private RowParser getLineParser(CsvParserBuilder builder) throws ReflectiveOperationException {
        Field parserFactoryField = CsvParserBuilder.class.getDeclaredField("parserFactory");
        parserFactoryField.setAccessible(true);
        Function<CsvParserBuilder, Function<String, List<Row>>> parserFactory = (Function<CsvParserBuilder, Function<String, List<Row>>>) parserFactoryField.get(builder);
        return (RowParser) parserFactory.apply(builder);
    }
}
