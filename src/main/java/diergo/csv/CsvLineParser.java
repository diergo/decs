package diergo.csv;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class CsvLineParser implements Function<String,String[]> {

    private static final String[] EMPTY_LINE = new String[0];

    private final Function<String,Character> determiner;
    private final char quote;
    private final String commentStart;
    private final StringBuffer formerLine = new StringBuffer();

    CsvLineParser(CharSequence separators, char quote, String commentStart) {
        this.determiner = separators.length() == 1 ? line -> separators.charAt(0) : new AutoSeparatorDeterminer(separators);
        this.quote = quote;
        this.commentStart = commentStart;
    }

    @Override
    public String[] apply(String line) {
        line = recoverFormerIncompleteLine(line);
        if (isEmpty(line)) {
            return EMPTY_LINE;
        }
        if (line.startsWith(commentStart)) {
            return new String[]{line};
        }
        char separator = determiner.apply(line);
        CharBuffer column = CharBuffer.allocate(line.length());
        List<String> columns = new ArrayList<>();
        boolean quoted = false;
        boolean isQuote = false;
        int i = 0;
        for (char c : line.toCharArray()) {
            if (c == separator && (!quoted || isQuote)) {
                columns.add(getValue(column));
                isQuote = false;
            } else if (c == quote) {
                if (isQuote) {
                    column.append(c);
                    isQuote = false;
                } else if (quoted) {
                    isQuote = true;
                } else if (column.position() == 0) {
                    quoted = true;
                } else {
                    throw new IllegalArgumentException("CSV need quoting when containing quote at " + i + ": " + line);
                }
            } else {
                column.append(c);
                isQuote = false;
            }
            ++i;
        }
        if (quoted && !isQuote) {
            formerLine.append(line);
            formerLine.append('\n');
            return null;
        }
        columns.add(getValue(column));
        return columns.toArray(new String[columns.size()]);
    }

    private String recoverFormerIncompleteLine(String line) {
        if (formerLine.length() > 0) {
            line = formerLine.append(line).toString();
            formerLine.delete(0, formerLine.length());
        }
        return line;
    }

    private boolean isEmpty(String line) {
        return line == null || line.trim().length() == 0;
    }

    private String getValue(CharBuffer value) {
        int length = value.position();
        value.rewind();
        value.limit(length);
        try {
            return value.toString();
        } finally {
            value.clear();
        }
    }

    private class AutoSeparatorDeterminer implements Function<String,Character> {
        private final CharSequence possibleSeparators;
        private Character separator = null;

        private AutoSeparatorDeterminer(CharSequence possibleSeparators) {
            this.possibleSeparators = possibleSeparators;
        }

        @Override
        public Character apply(String line) {
            if (separator != null) {
                return separator;
            }
            if (isEmpty(line)) {
                throw new IllegalStateException("Separator not determined");
            }
            separator = getBestVotedSeparator(voteForSeparators(line));
            return separator;
        }

        private Map<Character, Integer> voteForSeparators(String line) {
            Map<Character, Integer> votes = new HashMap<>();
            for (char c : possibleSeparators.toString().toCharArray()) {
                try {
                    votes.put(c, getFieldCountFromLineParsed(line));
                } catch (IllegalArgumentException e) {
                    votes.put(c, 0);
                }
            }
            return votes;
        }

        private char getBestVotedSeparator(Map<Character, Integer> votes) {
            return votes.entrySet().stream()
                    .reduce((e1, e2) -> e1.getValue() < e2.getValue() ? e2 : e1)
                    .get().getKey();
        }

        private int getFieldCountFromLineParsed(String line) {
            return CsvLineParser.this.apply(line).length;
        }
    }
}
