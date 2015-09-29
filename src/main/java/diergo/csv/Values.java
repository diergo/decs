package diergo.csv;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.function.BiFunction;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class Values {

    public static String valueAsString(Map<String,Object> values, String name) {
        Object value = values.get(name);
        return value == null ? null : String.valueOf(value);
    }

    public static BiFunction<Map<String,String>, String, Object> parsedValue(Map<String,Class<?>> types) {
        return (values, name) -> {
            String value = values.get(name);
            if (value == null) {
                return null;
            }
            Class<?> type = types.get(name);
            return type == null ? value : parseValue(value, type); 
        };
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
