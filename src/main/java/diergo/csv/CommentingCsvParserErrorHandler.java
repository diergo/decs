package diergo.csv;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;

/**
 * Handles parser errors by turning them into comments.
 *
 * @see CsvParserBuilder#handlingErrors(BiFunction)
 */
public class CommentingCsvParserErrorHandler implements BiFunction<RuntimeException, String, List<Row>> {

    /**
     * Creates a new error handler.
     */
    public static BiFunction<RuntimeException, String, List<Row>> commentingErrors() {
        return new CommentingCsvParserErrorHandler();
    }
    
    @Override
    public List<Row> apply(RuntimeException error, String line) {
        return asList(new Comment(error.getMessage()), new Comment(line));
    }
}
