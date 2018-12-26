package diergo.csv;

import java.util.function.Function;
import java.util.regex.Pattern;

class RowPrinter implements Function<Row, String> {

    final char separator;
    final char quote;
    final String commentStart;
    private final Pattern quotePattern;
    private final String quoteReplacement;

    RowPrinter(char separator, char quote, String commentStart) {
        this.separator = separator;
        this.quote = quote;
        this.commentStart = commentStart;
        quotePattern = Pattern.compile(String.valueOf(quote));
        quoteReplacement = new String(new char[]{quote, quote});
    }

    @Override
    public String apply(Row row) {
        StringBuilder line = new StringBuilder();
        if (commentStart != null && row.isComment()) {
            line.append(commentStart);
            line.append(row.iterator().next());
        } else {
            boolean first = true;
            for (String cell : row) {
                if (!first) {
                    line.append(separator);
                }
                if (cell != null) {
                    line.append(printValue(cell));
                }
                first = false;
            }
        }
        return line.toString();
    }

    private String printValue(String value) {
        boolean containsQuote = value.indexOf(quote) != -1;
        boolean quote = containsQuote || value.indexOf(separator) != -1 || (value.indexOf('\n') != -1 || value.indexOf('\r') != -1);
        if (quote) {
            if (containsQuote) {
                value = quotePattern.matcher(value).replaceAll(quoteReplacement);
            }
            return new StringBuilder(value.length() + 2).append(this.quote).append(value).append(this.quote).toString();
        }
        return value;
    }
}
