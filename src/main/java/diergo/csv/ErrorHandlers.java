package diergo.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Creates an error handler simply ignoring the error by skipping the line.
     */
    public static BiFunction<RuntimeException, String, List<Row>> ignoreErrors() {
        return (error, line) -> emptyList();
    }

    /**
     * Creates an error handler returning two comments with error and illegal line.
     */
    public static BiFunction<RuntimeException, String, List<Row>> commentingErrors() {
        return (error, line) -> asList(new Comment(error.getMessage()), new Comment(line));
    }

    /**
     * Creates an error handler logging the error and skipping the line. The
     * log is created with level WARN to logger of {@link CsvParserBuilder}.
     * <p>
     * This has a dependency to <a href="http://www.slf4j.org">SLF4J</a>!
     */
    public static BiFunction<RuntimeException, String, List<Row>> loggingErrors() {
        Logger log = LoggerFactory.getLogger(CsvParserBuilder.class);
        return (error, line) -> {
            log.warn("{}, the following line is skipped: {}", error.getMessage(), line);
            return emptyList();
        };
    }
}
