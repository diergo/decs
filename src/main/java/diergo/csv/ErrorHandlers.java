package diergo.csv;

import java.util.function.BiConsumer;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Error handlers to be used by the parser.
 *
 * @see CsvParserBuilder#handlingErrors(BiFunction)
 */
public class ErrorHandlers {

    /**
     * Creates an error handler simply throwing the error and stopping parsing.
     */
    public static BiFunction<String, RuntimeException, List<Row>> throwingError() {
        return (line, error) -> { throw error; };
    }

    /**
     * Creates an error handler simply ignoring the error by skipping the line.
     */
    public static BiFunction<String, RuntimeException, List<Row>> ignoreErrors() {
        return (line, error) -> emptyList();
    }

    /**
     * Creates an error handler returning two comments with error and illegal line.
     */
    public static BiFunction<String, RuntimeException, List<Row>> commentingErrors() {
        return (line, error) -> asList(new Comment(error.getMessage()), new Comment(line));
    }

    /**
     * Creates an error handler logging the error and skipping the line. The
     * log is created with level WARN to logger of {@link CsvParserBuilder}.
     * <p>
     * Using <a href="http://www.slf4j.org">SLF4J</a> this can be used like:
     * <pre>
     *     loggingErrors((line, error) -> LoggerFactory.getLogger("CSV").warn("{}, the following line is skipped: {}", error.getMessage(), line);
     * </pre>
     */
    public static BiFunction<String, RuntimeException, List<Row>> loggingErrors(BiConsumer<String, RuntimeException> logger) {
        return (line, error) -> {
            logger.accept(line, error);
            return emptyList();
        };
    }
}
