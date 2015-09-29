package diergo.csv;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Spliterators.spliterator;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;

public class Maps {

    public static Function<Row,List<Map<String,String>>> toMaps(List<String> header) {
        return new Row2MapFunction(header);
    }

    public static Function<Row,List<Map<String,String>>> toMaps() {
        return toMaps(null);
    }

    public static Function<Map<String,Object>, List<Row>> toRows(List<String> header) {
        return new Map2RowFunction(false, header);
    }

    public static Function<Map<String,Object>, List<Row>> toRowsWithHeader(List<String> header) {
        return new Map2RowFunction(true, header);
    }

    public static Function<Map<String,Object>, List<Row>> toRows() {
        return new Map2RowFunction(false, null);
    }

    public static Function<Map<String,Object>, List<Row>> toRowsWithHeader() {
        return new Map2RowFunction(true, null);
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
            Map<String, String> result = new HashMap<>();
            for (String value : values) {
                result.put(keys.get(i++), value);
            }
            return singletonList(result);
        }
    }

    private static class Map2RowFunction implements Function<Map<String, Object>, List<Row>> {

        private final AtomicReference<List<String>> header;
        private final AtomicBoolean headerNeeded;

        public Map2RowFunction(boolean includeHeader, List<String> header) {
            this.header = new AtomicReference<>(header);
            this.headerNeeded = new AtomicBoolean(includeHeader);
        }

        @Override
        @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
        public List<Row> apply(Map<String, Object> values) {
            List<Row> result = new ArrayList<>();
            List<String> headers = header.get();
            if (headers == null && header.compareAndSet(null, values.keySet().stream().collect(toList()))) {
                // after compareAndSet header is set in any case, so header will never be null
                headers = header.get();
            }
            if (headerNeeded.compareAndSet(true, false)) {
                result.add(new Columns(headers));
            }
            List<String> columns = new ArrayList<>();
            for (String key : headers) {
                Object value = values.get(key);
                columns.add(value == null ? null : String.valueOf(value));
            }
            result.add(new Columns(columns));
            return result;
        }
    }
}
