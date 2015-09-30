package diergo.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiFunction;

import static java.util.Collections.emptyList;

/**
 * Handles parser errors by logging and skipping them.
 *
 * @see CsvParserBuilder#handlingErrors(BiFunction)
 */
public class LoggingCsvParserErrorHandler implements BiFunction<RuntimeException, String, List<Row>> {

    /**
     * Creates a new error handler.
     */
    public static BiFunction<RuntimeException, String, List<Row>> loggingErrors() {
        return new LoggingCsvParserErrorHandler();
    }

    private static final Logger LOG = LoggerFactory.getLogger(LoggingCsvParserErrorHandler.class);

    @Override
    public List<Row> apply(RuntimeException error, String line) {
        LOG.warn("{}, the following line is skipped: {}", error.getMessage(), line);
        return emptyList();
    }
}
