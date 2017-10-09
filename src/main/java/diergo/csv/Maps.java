package diergo.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

/**
 * Helpers to work with {@link Row}s and {@link Map}s.
 *
 * @see java.util.stream.Stream#map(Function)
 */
public class Maps {

    /**
     * A mapper to convert rows to data maps using the columns names in header.
     */
    public static Function<Row, List<Map<String, String>>> toMaps(List<String> header) {
        return new Row2MapFunction(header);
    }

    /**
     * A mapper to convert rows to data maps using the columns names from the first row.
     */
    public static Function<Row, List<Map<String, String>>> toMaps() {
        return toMaps(null);
    }

    /**
     * A mapper to convert data maps to rows using the columns order in header. Other map values are
     * ignored. The columns names are not written as header.
     */
    public static Function<Map<String, String>, Row> toRows(List<String> header) {
        return new Map2RowFunction<>(false, header, rows -> rows.get(0));
    }

    /**
     * A mapper to convert data maps to rows using the columns order in header. Other map values are
     * ignored. The columns names are written as header. The first result will contain two rows, the
     * remaining ones one row.
     *
     * @see java.util.stream.Stream#flatMap(Function)
     */
    public static Function<Map<String, String>, List<Row>> toRowsWithHeader(List<String> header) {
        return new Map2RowFunction<>(true, header, identity());
    }

    /**
     * A mapper to convert data maps to rows using the keys of the first data map as columns names.
     * The columns names are not written as header.
     */
    public static Function<Map<String, String>, Row> toRows() {
        return new Map2RowFunction<>(false, null, rows -> rows.get(0));
    }

    /**
     * A mapper to convert data maps to rows using the keys of the first data map as columns names.
     * The columns names are written as header. The first result will contain two rows, the
     * remaining ones one row.
     *
     * @see java.util.stream.Stream#flatMap(Function)
     */
    public static Function<Map<String, String>, List<Row>> toRowsWithHeader() {
        return new Map2RowFunction<>(true, null, identity());
    }

    /**
     * A mapper for data maps by converting all values using a function.
     *
     * @param <S>      the source value type
     * @param <T>      the target value type
     * @param supplier creating the result typee, e.g. {@link HashMap#HashMap(Map)}
     * @see Values
     */
    public static <S, T> Function<Map<String, S>, Map<String, T>> withValuesMapped(Supplier<Map<String, T>> supplier, BiFunction<Map<String, S>, String, ? extends T> valueMapper) {
        return row -> {
            Map<String, T> result = supplier.get();
            for (Map.Entry<String, S> entry : row.entrySet()) {
                result.put(entry.getKey(), valueMapper.apply(row, entry.getKey()));
            }
            return result;
        };
    }

    /**
     * A mapper for data maps by removing the entries for a key returning a new map.
     *
     * @param <V>      the value type
     * @param supplier creating the result from the source, e.g. {@link HashMap#HashMap(Map)} or
     *                 {@link UnaryOperator#identity()}
     * @since 3.1.0
     */
    public static <V> Function<Map<String, V>, Map<String, V>> removingValue(UnaryOperator<Map<String, V>> supplier, String... keys) {
        return row -> {
            Map<String, V> result = supplier.apply(row);
            for (String key : keys) {
                result.remove(key);
            }
            return result;
        };
    }

    /**
     * A mapper for data maps by renaming the key of entries returning a new map.
     *
     * @param <V>      the value type
     * @param supplier creating the result from the source, e.g. {@link HashMap#HashMap(Map)} or
     *                 {@link UnaryOperator#identity()}
     * @since 3.1.0
     */
    public static <V> Function<Map<String, V>, Map<String, V>> renamingValue(UnaryOperator<Map<String, V>> supplier, String oldKey, String newKey) {
        return row -> {
            Map<String, V> result = supplier.apply(row);
            result.put(newKey, result.remove(oldKey));
            return result;
        };
    }

    /**
     * A mapper for data maps by adding new entries for a key creating a value from the former
     * values returning a new map.
     *
     * @param <V>      the value type
     * @param supplier creating the result from the source, e.g. {@link HashMap#HashMap(Map)} or
     *                 {@link UnaryOperator#identity()}
     * @since 3.1.0
     */
    public static <V> Function<Map<String, V>, Map<String, V>> addingValue(UnaryOperator<Map<String, V>> supplier, String key, Function<Map<String, V>, ? extends V> valueCreator) {
        return row -> {
            Map<String, V> result = supplier.apply(row);
            result.put(key, valueCreator.apply(row));
            return result;
        };
    }

    private Maps() {
    }

    private static class Row2MapFunction implements Function<Row, List<Map<String, String>>> {

        private final AtomicReference<List<String>> header;

        Row2MapFunction(List<String> header) {
            this.header = new AtomicReference<>(header);
        }

        @Override
        public List<Map<String, String>> apply(Row values) {
            if (values.isComment()) {
                return emptyList();
            }
            if (header.compareAndSet(null, stream(values.spliterator(), false).collect(toList()))) {
                return emptyList();
            }
            List<String> keys = header.get();
            int i = 0;
            Map<String, String> result = new HashMap<>();
            for (String value : values) {
                result.put(keys.get(i++), value);
            }
            return singletonList(result);
        }
    }

    private static class Map2RowFunction<R> implements Function<Map<String, String>, R> {

        private final AtomicReference<List<String>> header;
        private final Function<List<Row>, R> resultMapper;
        private final AtomicBoolean headerNeeded;

        Map2RowFunction(boolean includeHeader, List<String> header, Function<List<Row>, R> resultMapper) {
            this.header = new AtomicReference<>(header);
            this.resultMapper = resultMapper;
            this.headerNeeded = new AtomicBoolean(includeHeader);
        }

        @Override
        @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
        public R apply(Map<String, String> values) {
            List<String> headers = header.get();
            if (headers == null && header.compareAndSet(null, new ArrayList<>(values.keySet()))) {
                // after compareAndSet header is set in any case, so header will never be null
                headers = header.get();
            }
            List<Row> result = new ArrayList<>();
            if (headerNeeded.compareAndSet(true, false)) {
                result.add(new Cells(headers));
            }
            List<String> columns = new ArrayList<>();
            for (String key : headers) {
                columns.add(values.get(key));
            }
            result.add(new Cells(columns));
            return resultMapper.apply(result);
        }
    }
}
