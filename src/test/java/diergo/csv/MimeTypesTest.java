package diergo.csv;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@SuppressWarnings("deprecation")
class MimeTypesTest {

    private MimeType csv;

    @Test
    void mimeTypeWithoutCharsetIsUSASCII() {
        assertThat(MimeTypes.getCharset(csv), is(StandardCharsets.US_ASCII));
    }

    @Test
    void mimeTypeWithCharsetIsParsed() {
        csv.setParameter("charset", "UTF-8");
        assertThat(MimeTypes.getCharset(csv), is(StandardCharsets.UTF_8));
    }

    @Test
    void mimeTypeWithoutHeaders() {
        assertThat(MimeTypes.includesHeaders(csv), is(false));
    }

    @Test
    void mimeTypeWithHeadersAbsent() {
        csv.setParameter("headers", "absent");
        assertThat(MimeTypes.includesHeaders(csv), is(false));
    }

    @Test
    void mimeTypeWithHeadersPresent() {
        csv.setParameter("headers", "present");
        assertThat(MimeTypes.includesHeaders(csv), is(true));
    }

    @Test
    void createDefaultMimeType() {
        csv.setParameter("charset", "US-ASCII");
        csv.setParameter("headers", "absent");
        assertThat(MimeTypes.mimeType().build().toString(), is(csv.toString()));
    }

    @Test
    void createMimeTypeWithHeaders() {
        csv.setParameter("charset", "US-ASCII");
        csv.setParameter("headers", "present");
        assertThat(MimeTypes.mimeType().includeHeaders().build().toString(), is(csv.toString()));
    }

    @Test
    void createMimeTypeWithCharset() {
        csv.setParameter("charset", "UTF-8");
        csv.setParameter("headers", "absent");
        assertThat(MimeTypes.mimeType().encoded(StandardCharsets.UTF_8).build().toString(), is(csv.toString()));
    }

    @BeforeEach
    void createMimeType() throws MimeTypeParseException {
        csv = new MimeType("text/csv");
    }
}
