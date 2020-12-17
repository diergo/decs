package diergo.csv;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Helpers to read or create MIME types according to
 * <a href="https://tools.ietf.org/html/rfc4180#section-3">RFC 4180</a>.
 */
@Deprecated
public class MimeTypes {

    public static final String CHARSET = "charset";
    public static final String HEADERS = "headers";
    public static final String HEADERS_PRESENT = "present";

    /**
     * Returns the specified charset using the parameter {@link #CHARSET}.
     * Default is {@link java.nio.charset.StandardCharsets#US_ASCII}.
     */
    public static Charset getCharset(MimeType type) {
        String name = type.getParameter(CHARSET);
        return name == null ? US_ASCII : Charset.forName(name);
    }

    /**
     * Returns the specified charset using the parameter {@link #HEADERS}.
     * Default is {@code false}.
     */
    public static boolean includesHeaders(MimeType type) {
        return HEADERS_PRESENT.equals(type.getParameter(HEADERS));
    }

    /**
     * Returns a builder to create a {@code text/csv} MIME type.
     */
    public static Builder mimeType() {
        return new Builder();
    }

    /**
     * Create a MIME type with fluent API.
     *
     * @see #mimeType()
     */
    public static class Builder {

        private boolean headers = false;
        private Charset charset = US_ASCII;

        private Builder() {
        }

        /**
         * Sets the parameter {@link #HEADERS} to {@code present}.
         */
        public Builder includeHeaders() {
            this.headers = true;
            return this;
        }

        /**
         * Sets the {@link #CHARSET} parameter.
         */
        public Builder encoded(Charset charset) {
            this.charset = charset;
            return this;
        }

        /**
         * Creates the parametrized MIME type.
         */
        public MimeType build() {
            try {
                MimeType type = new MimeType("text", "csv");
                type.setParameter(CHARSET, charset.name());
                type.setParameter(HEADERS, headers ? HEADERS_PRESENT : "absent");
                return type;
            } catch (MimeTypeParseException e) {
                throw new IllegalStateException("cannot create MIME type text/csv");
            }
        }
    }
}
