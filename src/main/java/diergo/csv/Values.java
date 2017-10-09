package diergo.csv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

/**
 * Helpers to work with data {@link Map}s.
 *
 * @see Maps#withValuesMapped(Supplier, BiFunction)
 */
public class Values {

    /**
     * Converts the value with the key to its string representation.
     */
    public static String valueAsString(Map<String,Object> values, String key) {
        Object value = values.get(key);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * Converts the value with the key by converting it function returned by the supplier.
     * The returning function returns {@code null} for a missing converter.
     * @param <S> the source value type
     * @param <T> the target value type
     * @since 3.1.0
     */
    public static <S,T> BiFunction<Map<String,S>, String, T> convertedValue(Function<String,Function<S,? extends T>> converterSupplier) {
        return (values, name) -> {
            S value = values.get(name);
            if (value == null) {
                return null;
            }
            Function<S,? extends T> converter = converterSupplier.apply(name);
            return converter == null ? null : converter.apply(value);
        };
    }

    /**
     * Converts the value with the key by parsing it according to the target type.
     * The supported types are: {@link Integer}, {@link Double}, {@link Float},
     * {@link BigDecimal}, {@link BigInteger}, {@link Boolean} and {@link String}
     */
    public static BiFunction<Map<String,String>, String, Object> parsedValue(Map<String,Class<?>> types) {
        return convertedValue(name -> value -> parseValue(value, types.getOrDefault(name, String.class)));
    }
    
    private Values() {
    }

    private static Object parseValue(String value, Class<?> type) {
        if (type.isEnum()) {
            return parseEnum(value, type);
        }
        switch (type.getName()) {
            case "java.lang.String":
                return value;
            case "java.lang.Boolean":
                return parseBoolean(value);
            case "java.lang.Float":
                return parseFloat(value);
            case "java.lang.Double":
                return parseDouble(value);
            case "java.lang.Integer":
                return parseInt(value);
            case "java.math.BigDecimal":
                return new BigDecimal(value);
            case "java.math.BigInteger":
                return new BigInteger(value);
            default:
                throw new IllegalArgumentException("unsupported value type: " + type);
        }
    }
    
    private static <E extends Enum<E>> E parseEnum(String value, Class<?> type) {
        @SuppressWarnings("unchecked")
        Class<E> enumType = (Class<E>) type;
        return Enum.valueOf(enumType, value);
    }
}
