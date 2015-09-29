package diergo.csv;

import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class Rows {

    public static Row trim(Row row) {
        return row.isComment() ? row : new Columns(stream(row.spliterator(), false)
            .map(column -> column == null ? null : column.trim()).collect(toList()));
    }

    public static Row replaceEmptyWithNull(Row row) {
        return row.isComment() ? row : new Columns(stream(row.spliterator(), false)
            .map(column -> (column == null || column.length() == 0) ? null : column).collect(toList()));
    }

    public static Row replaceNullWithEmpty(Row row) {
        return row.isComment() ? row : new Columns(stream(row.spliterator(), false)
            .map(column -> column == null ? "" : column).collect(toList()));
    }

    private Rows() {
    }
}
