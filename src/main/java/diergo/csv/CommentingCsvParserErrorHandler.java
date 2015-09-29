package diergo.csv;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;

public class CommentingCsvParserErrorHandler implements BiFunction<RuntimeException, String, List<Row>> {

    public static BiFunction<RuntimeException, String, List<Row>> commentingErrors() {
        return new CommentingCsvParserErrorHandler();
    }
    
    @Override
    public List<Row> apply(RuntimeException error, String line) {
        return asList(new Comment(error.getMessage()), new Comment(line));
    }
}
