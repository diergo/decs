package diergo.csv;

import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static diergo.csv.CsvReaderBuilder.toCsvStream;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CsvReaderBuilderTest {

    private Function<String, String[]> reader;
    
    @Test
    public void byDefaultALineParserIsCreated() throws ReflectiveOperationException {
        CsvReaderBuilder builder = toCsvStream(new StringReader(""));
        CsvLineParser parser = getLineParser(builder);

        assertThat(parser.quote, is('"'));
        assertThat(parser.commentStart, is("#"));
    }

    @Test(expected = IllegalStateException.class)
    public void byDefaultAnAutoSeparatorDeterminerIsConfiguredWhichCannotHandleAnEmptyLine() throws ReflectiveOperationException {
        CsvReaderBuilder builder = toCsvStream(new StringReader(""));
        CsvLineParser parser = getLineParser(builder);
        parser.determiner.apply("");
    }

    @Test
    public void allConfigurationsArePassedToParser() throws ReflectiveOperationException {
        CsvReaderBuilder builder = toCsvStream(new StringReader("")).commentsStartWith("//").quotedWith('\'').separatedBy(',');
        CsvLineParser parser = getLineParser(builder);

        assertThat(parser.quote, is('\''));
        assertThat(parser.commentStart, is("//"));
        assertThat(parser.determiner.apply(""), is(','));
    }

    @Test
    public void anEmptyReaderCreatesAnEmptyStream() {
        Stream<String[]> rows = builderWithMockReader("").build();

        assertThat(rows.count(), is(0L));
        
        verify(reader, never()).apply(anyString());
    }

    @Test
    public void commentsAreSkippedOptionally() {
        Stream<String[]> rows = builderWithMockReader("#comment;no columns")
            .skipComments().build();

        assertThat(rows.count(), is(0L));

        verify(reader, never()).apply(anyString());
    }

    @Test
    public void commentsStartCanBeConfigured() {
        Stream<String[]> rows = builderWithMockReader("//omment;no columns")
            .skipComments().commentsStartWith("//").build();

        assertThat(rows.count(), is(0L));

        verify(reader, never()).apply(anyString());
    }

    @Test
    public void theParserIsCalledForEveryLine() {
        Stream<String[]> rows = builderWithMockReader("line;1\nline;2").build();
        when(reader.apply(anyString()))
            .thenAnswer(invocation -> new String[] {"line", "n"});

        assertThat(rows.count(), is(2L));

        verify(reader).apply(eq("line;1"));
        verify(reader).apply(eq("line;2"));
    }

    @Test
    public void valuesAreTrimmedOptional() {
        Stream<String[]> rows = builderWithMockReader("line \n line").trimValues().build();
        when(reader.apply(anyString()))
            .thenAnswer(invocation -> new String[] {" line "});

        List<String[]> result = rows.collect(toList());
        assertThat(result, everyItem(is(new String[] {"line"})));
    }

    @Test
    public void emptyValuesAreReplacesByNull() {
        Stream<String[]> rows = builderWithMockReader("\n").treatEmptyAsNull().build();
        when(reader.apply(anyString()))
            .thenAnswer(invocation -> new String[] {""});

        List<String[]> result = rows.collect(toList());
        assertThat(result, everyItem(is(new String[] {null})));
    }

    @Test
    public void trimIsDoneBeforeNullReplacement() {
        Stream<String[]> rows = builderWithMockReader("  \n ").treatEmptyAsNull().trimValues().build();
        when(reader.apply(anyString()))
            .thenAnswer(invocation -> new String[] {""});

        List<String[]> result = rows.collect(toList());
        assertThat(result, everyItem(is(new String[] {null})));
    }

    @Before
    @SuppressWarnings("unchecked")
    public void createReaderMock() {
        reader = mock(Function.class); 
    }
    
    private CsvReaderBuilder builderWithMockReader(String csv) {
        return toCsvStream(new StringReader(csv)).usingParser(builder -> reader);
    }

    @SuppressWarnings("unchecked")
    private CsvLineParser getLineParser(CsvReaderBuilder builder) throws ReflectiveOperationException {
        Field parserFactoryField = CsvReaderBuilder.class.getDeclaredField("parserFactory");
        parserFactoryField.setAccessible(true);
        Function<CsvReaderBuilder, Function<String, String[]>> parserFactory = (Function<CsvReaderBuilder, Function<String, String[]>>) parserFactoryField.get(builder);
        return (CsvLineParser) parserFactory.apply(builder);
    }
}
