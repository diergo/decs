package diergo.csv;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import static diergo.csv.Values.parsedValue;
import static diergo.csv.Values.valueAsString;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(DataProviderRunner.class)
public class ValuesTest {

    @Test
    @UseDataProvider("valueAsStringProvider")
    public void knownValueAsStringIsPrinted(Object value, String expected) {
        assertThat(valueAsString(singletonMap("test", value), "test"), is(expected));
    }

    @Test
    public void unknownValueAsStringIsNull() {
        assertThat(valueAsString(singletonMap("test", 1), "unknown"), nullValue());
    }

    @Test
    public void knownValueParsedNullKeepsNull() {
        assertThat(parsedValue(emptyMap()).apply(singletonMap("test", null), "test"), nullValue());
    }

    @Test
    public void knownValueParsedUnchanged() {
        assertThat(parsedValue(emptyMap()).apply(singletonMap("test", "1"), "test"), is("1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void knownValueParsedToUnsupportedTypeRaisesError() {
        parsedValue(singletonMap("test", Row.class)).apply(singletonMap("test", "ha,ha"), "test");
    }

    @Test
    @UseDataProvider("parsedValueProvider")
    public void knownValueParsedAsTargetType(String value, Class<?> type, Object expected) {
        assertThat(parsedValue(singletonMap("test", type)).apply(singletonMap("test", value), "test"), is(expected));
    }
    
    @DataProvider
    public static Object[][] valueAsStringProvider() {
        return new Object[][] {
            {"foo", "foo"},
            {1, "1"},
            {null, null},
        };
    }
    
    @DataProvider
    public static Object[][] parsedValueProvider() {
        return new Object[][] {
            {"1", Integer.class, 1},
            {"1.5", Float.class, 1.5f},
            {"1.5", Double.class, 1.5},
            {"8", BigInteger.class, new BigInteger("8")},
            {"5.43", BigDecimal.class, new BigDecimal("5.43")},
            {"DAYS", TimeUnit.class, TimeUnit.DAYS},
            {"true", Boolean.class, true},
            {"false", Boolean.class, false},
            {"foo", String.class, "foo"}
        };
    }
}
