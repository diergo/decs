package diergo.csv;

public enum Option {

    /**
     * trim values after read and before generated
     */
    TRIM,
    /**
     * commented lines (starting with #) are skipped on read
     */
    COMMENTS_SKIPPED,
    /**
     * an empty value will read as null, null will be generated as empty
     */
    EMPTY_AS_NULL;

    public final static char QUOTE = '"';
}
