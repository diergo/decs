package diergo.csv;

import org.junit.Before;
import org.junit.Test;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import java.nio.charset.StandardCharsets;

import static diergo.csv.MimeTypes.getCharset;
import static diergo.csv.MimeTypes.includesHeaders;
import static diergo.csv.MimeTypes.mimeType;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MimeTypesTest {

    private MimeType csv;

    @Test
    public void mimeTypeWithoutCharsetIsUSASCII() {
        assertThat(getCharset(csv), is(StandardCharsets.US_ASCII));
    }

    @Test
    public void mimeTypeWithCharsetIsParsed() {
        csv.setParameter("charset", "UTF-8");
        assertThat(getCharset(csv), is(StandardCharsets.UTF_8));
    }

    @Test
    public void mimeTypeWithoutHeaders() {
        assertThat(includesHeaders(csv), is(false));
    }

    @Test
    public void mimeTypeWithHeadersAbsent() {
        csv.setParameter("headers", "absent");
        assertThat(includesHeaders(csv), is(false));
    }

    @Test
    public void mimeTypeWithHeadersPresent() {
        csv.setParameter("headers", "present");
        assertThat(includesHeaders(csv), is(true));
    }

    @Test
    public void createDefaultMimeType() {
        csv.setParameter("charset", "US-ASCII");
        csv.setParameter("headers", "absent");
        assertThat(mimeType().build().toString(), is(csv.toString()));
    }

    @Test
    public void createMimeTypeWithHeaders() {
        csv.setParameter("charset", "US-ASCII");
        csv.setParameter("headers", "present");
        assertThat(mimeType().includeHeaders().build().toString(), is(csv.toString()));
    }

    @Test
    public void createMimeTypeWithCharset() {
        csv.setParameter("charset", "UTF-8");
        csv.setParameter("headers", "absent");
        assertThat(mimeType().encoded(StandardCharsets.UTF_8).build().toString(), is(csv.toString()));
    }

    @Before
    public void createMimeType() throws MimeTypeParseException {
        csv = new MimeType("text/csv");
    }
}
