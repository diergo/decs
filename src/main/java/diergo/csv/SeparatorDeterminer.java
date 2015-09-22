package diergo.csv;

/**
 * Determines the separator for the fields of a CSV line.
 *
 * @see diergo.csv.CsvReaderStream.Builder
 * @since 1.1
 */
interface SeparatorDeterminer {
    /**
     * Determines the separator for the line.
     *
     * @param line the line to be separated, the behavior is undefined for an empty
     *             line.
     */
    public char determineSeparator(String line);
}
