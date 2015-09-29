package diergo.csv;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Helpers to work with {@link Row}s.
 */
public class Rows {

    /**
     * A filter to exclude comments.
     * @see java.util.stream.Stream#filter(Predicate) 
     */
    public static boolean withoutComments(Row row) {
        return !row.isComment();
    }

    /**
     * A mapper to trim column data.
     * @see java.util.stream.Stream#map(Function) 
     */
    public static Row trim(Row row) {
        return row.isComment() ? row : new Columns(stream(row.spliterator(), false)
            .map(column -> column == null ? null : column.trim()).collect(toList()));
    }

    /**
     * A mapper to replace empty column data with {@code null}.
     * @see java.util.stream.Stream#map(Function)
     */
    public static Row replaceEmptyWithNull(Row row) {
        return row.isComment() ? row : new Columns(stream(row.spliterator(), false)
            .map(column -> (column == null || column.length() == 0) ? null : column).collect(toList()));
    }

    /**
     * A mapper to replace null column data with an empty string ({@code ""}).
     * @see java.util.stream.Stream#map(Function)
     */
    public static Row replaceNullWithEmpty(Row row) {
        return row.isComment() ? row : new Columns(stream(row.spliterator(), false)
            .map(column -> column == null ? "" : column).collect(toList()));
    }

    private Rows() {
    }
}
