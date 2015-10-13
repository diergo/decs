package diergo.csv;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Helpers to work with {@link Row}s.
 */
public class Rows {

    private static final String[] NO_CELLS = new String[0];

    /**
     * A filter to exclude comments.
     * @see java.util.stream.Stream#filter(Predicate) 
     */
    public static boolean withoutComments(Row row) {
        return !row.isComment();
    }

    /**
     * Creates a mapper to map cell data one by one.
     * @see java.util.stream.Stream#map(Function)
     */
    public static UnaryOperator<Row> rows(UnaryOperator<String> cellMapper) {
        return row -> row.isComment() ? row : new Cells(stream(row.spliterator(), false)
            .map(cellMapper).collect(toList()));
    }

    /**
     * A mapper to trim column data.
     * @see #rows(UnaryOperator) 
     */
    public static UnaryOperator<String> trimCell() {
        return cell -> cell == null ? null : cell.trim();
    }

    /**
     * A mapper to replace empty cells with {@code null}.
     * @see #rows(UnaryOperator)
     */
    public static UnaryOperator<String> emptyCellToNull() {
        return cell -> (cell == null || cell.length() == 0) ? null : cell;
    }

    /**
     * A mapper to replace {@code null} with empty cells.
     * @see #rows(UnaryOperator)
     */
    public static UnaryOperator<String> nullToEmptyCell() {
        return cell -> cell == null ? "" : cell;
    }

    /**
     * A mapper to generate cell values as array.
     * @see java.util.stream.Stream#map(Function)
     * @since 3.1.0
     */
    public static String[] toStringArray(Row row) {
        return row.isComment() ? NO_CELLS : stream(row.spliterator(), false).toArray(String[]::new);
    }

    private Rows() {
    }
}
