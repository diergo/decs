package diergo.csv;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static diergo.csv.Values.convertedValue;
import static diergo.csv.Values.parsedValue;
import static diergo.csv.Values.valueAsString;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValuesTest {

    @ParameterizedTest(name = "knownValueAsStringIsPrinted({arguments})")
    @MethodSource("valueAsStringProvider")
    void knownValueAsStringIsPrinted(Object value, String expected) {
        assertThat(valueAsString(singletonMap("test", value), "test"), is(expected));
    }

    @Test
    void unknownValueAsStringIsNull() {
        assertThat(valueAsString(singletonMap("test", 1), "unknown"), nullValue());
    }

    @Test
    void knownValueParsedNullKeepsNull() {
        assertThat(parsedValue(emptyMap()).apply(singletonMap("test", null), "test"), nullValue());
    }

    @Test
    void knownValueParsedUnchanged() {
        assertThat(parsedValue(emptyMap()).apply(singletonMap("test", "1"), "test"), is("1"));
    }

    @Test
    void knownValueParsedToUnsupportedTypeRaisesError() {
        assertThrows(IllegalArgumentException.class,
                () -> parsedValue(singletonMap("test", Row.class)).apply(singletonMap("test", "ha,ha"), "test"));
    }

    @ParameterizedTest(name = "knownValueParsedAsTargetType({arguments})")
    @MethodSource("parsedValueProvider")
    void knownValueParsedAsTargetType(String value, Class<?> type, Object expected) {
        assertThat(parsedValue(singletonMap("test", type)).apply(singletonMap("test", value), "test"), is(expected));
    }

    @Test
    void valuesAreConvertedOrReplacedByNull() {
        BiFunction<Map<String, String>, String, Boolean> converter = convertedValue(name -> "test".equals(name) ? (value -> value.contains("test")) : null);
        assertThat(converter.apply(singletonMap("test", "my test value"), "test"), is(true));
        assertThat(converter.apply(singletonMap("test", "other value"), "test"), is(false));
        assertThat(converter.apply(singletonMap("test", "other value"), "foo"), nullValue());
    }

    static Stream<Arguments> valueAsStringProvider() {
        return Stream.of(
                Arguments.of("foo", "foo"),
                Arguments.of(1, "1"),
                Arguments.of(null, null));
    }

    static Stream<Arguments> parsedValueProvider() {
        return Stream.of(
                Arguments.of("1", Integer.class, 1),
                Arguments.of("1.5", Float.class, 1.5f),
                Arguments.of("1.5", Double.class, 1.5),
                Arguments.of("8", BigInteger.class, new BigInteger("8")),
                Arguments.of("5.43", BigDecimal.class, new BigDecimal("5.43")),
                Arguments.of("DAYS", TimeUnit.class, TimeUnit.DAYS),
                Arguments.of("true", Boolean.class, true),
                Arguments.of("false", Boolean.class, false),
                Arguments.of("foo", String.class, "foo"));
    }
}
