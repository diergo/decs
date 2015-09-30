package diergo.csv;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Spliterators.spliterator;
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
    public static Function<Row,List<Map<String,String>>> toMaps(List<String> header) {
        return new Row2MapFunction(header);
    }

    /**
     * A mapper to convert rows to data maps using the columns names from the first row.
     */
    public static Function<Row,List<Map<String,String>>> toMaps() {
        return toMaps(null);
    }

    /**
     * A mapper to convert data maps to rows using the columns order in header.
     * Other map values are ignored. The columns names are not written as header.
     */
    public static Function<Map<String,String>, List<Row>> toRows(List<String> header) {
        return new Map2RowFunction(false, header);
    }

    /**
     * A mapper to convert data maps to rows using the columns order in header.
     * Other map values are ignored. The columns names are written as header.
     */
    public static Function<Map<String,String>, List<Row>> toRowsWithHeader(List<String> header) {
        return new Map2RowFunction(true, header);
    }

    /**
     * A mapper to convert data maps to rows using the keys of the first data map as columns names.
     * The columns names are not written as header.
     */
    public static Function<Map<String,String>, List<Row>> toRows() {
        return new Map2RowFunction(false, null);
    }

    /**
     * A mapper to convert data maps to rows using the keys of the first data map as columns names.
     * The columns names are written as header.
     */
    public static Function<Map<String,String>, List<Row>> toRowsWithHeader() {
        return new Map2RowFunction(true, null);
    }

    /**
     * A mapper for data maps by converting all values using a function.
     * @param <S> the source value type
     * @param <T> the target value type
     * @see Values
     */
    public static <S,T> Function<Map<String,S>, Map<String,T>> withValuesMapped(BiFunction<Map<String,S>,String,T> valueMapper) {
        return new ValueMapperFunction<>(valueMapper);
    }

    private Maps() {
    }
    
    private static class Row2MapFunction implements Function<Row, List<Map<String,String>>> {

        private final AtomicReference<List<String>> header;

        public Row2MapFunction(List<String> header) {
            this.header = new AtomicReference<>(header);
        }

        @Override
        public List<Map<String,String>> apply(Row values) {
            if (values.isComment()) {
                return emptyList();
            }
            if (header.get() == null && header.compareAndSet(null,
                stream(spliterator(values.iterator(), values.getLength(), 0), false).collect(toList()))) {
                return Collections.emptyList();
            }
            List<String> keys = header.get();
            int i = 0;
            Map<String, String> result = new LinkedHashMap<>();
            for (String value : values) {
                result.put(keys.get(i++), value);
            }
            return singletonList(result);
        }
    }

    private static class Map2RowFunction implements Function<Map<String, String>, List<Row>> {

        private final AtomicReference<List<String>> header;
        private final AtomicBoolean headerNeeded;

        public Map2RowFunction(boolean includeHeader, List<String> header) {
            this.header = new AtomicReference<>(header);
            this.headerNeeded = new AtomicBoolean(includeHeader);
        }

        @Override
        @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
        public List<Row> apply(Map<String, String> values) {
            List<Row> result = new ArrayList<>();
            List<String> headers = header.get();
            if (headers == null && header.compareAndSet(null, values.keySet().stream().collect(toList()))) {
                // after compareAndSet header is set in any case, so header will never be null
                headers = header.get();
            }
            if (headerNeeded.compareAndSet(true, false)) {
                result.add(new Cells(headers));
            }
            List<String> columns = new ArrayList<>();
            for (String key : headers) {
                String value = values.get(key);
                columns.add(value == null ? null : value);
            }
            result.add(new Cells(columns));
            return result;
        }
    }

    private static class ValueMapperFunction<S,T> implements Function<Map<String, S>, Map<String, T>> {

        private final BiFunction<Map<String,S>,String,T> valueMapper;

        public ValueMapperFunction(BiFunction<Map<String,S>,String,T> valueMapper) {
            this.valueMapper = valueMapper;
        }

        @Override
        public Map<String, T> apply(Map<String, S> source) {
            Map<String, T> result = new LinkedHashMap<>();
            for (Map.Entry<String,S> entry : source.entrySet()) {
                result.put(entry.getKey(), valueMapper.apply(source, entry.getKey()));
            }
            return result;
        }
    }
}
