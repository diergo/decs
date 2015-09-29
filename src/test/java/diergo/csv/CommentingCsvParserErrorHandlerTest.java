package diergo.csv;

import org.junit.Test;

import java.util.List;

import static diergo.csv.CommentingCsvParserErrorHandler.commentingErrors;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CommentingCsvParserErrorHandlerTest {

    @Test
    public void handlerReturnsNoRows() {
        List<Row> result = commentingErrors().apply(new IllegalArgumentException("error"), "foo,bar");
        assertThat(result, is(asList(new Comment("error"), new Comment("foo,bar"))));
    }

}
