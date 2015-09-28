package diergo.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return new Row2MapFunction(null);
    }

    public static Function<Map<String,String>, List<Row>> toRows(List<String> header) {
        return new Map2RowFunction(header);
    }

    public static Function<Map<String,String>, List<Row>> toRows() {
        return new Map2RowFunction(null);
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

    private static class Map2RowFunction implements Function<Map<String, String>, List<Row>> {

        private final AtomicReference<List<String>> header;

        public Map2RowFunction(List<String> header) {
            this.header = new AtomicReference<>(header);
        }

        @Override
        public List<Row> apply(Map<String, String> values) {
            List<Row> result = new ArrayList<>();
            if (header.get() == null && header.compareAndSet(null, values.keySet().stream().collect(toList()))) {
                result.add(new Columns(header.get()));
            }
            List<String> columns = new ArrayList<>();
            for (String key : header.get()) {
                columns.add(values.get(key));
            }
            result.add(new Columns(columns));
            return result;
        }
    }
}
