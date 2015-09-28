package diergo.csv;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

class RowParser implements Function<String,List<Row>> {

    private static final Row EMPTY_LINE = new Columns();

    final Function<String,Character> determiner;
    final char quote;
    final String commentStart;
    final boolean laxMode;
    private final AtomicReference<String> formerLine = new AtomicReference<>();
    private final AtomicInteger lineNo = new AtomicInteger(0);

    RowParser(CharSequence separators, char quote, String commentStart, boolean laxMode) {
        this.determiner = separators.length() == 1 ? line -> separators.charAt(0) : new AutoSeparatorDeterminer(separators);
        this.quote = quote;
        this.commentStart = commentStart;
        this.laxMode = laxMode;
    }

    @Override
    public List<Row> apply(String line) {
        lineNo.incrementAndGet();
        line = recoverFormerIncompleteLine(line);
        if (isEmpty(line)) {
            return singletonList(EMPTY_LINE);
        }
        List<Row> rows = parseLine(line, determiner.apply(line));
        if (rows.isEmpty()) {
            formerLine.compareAndSet(null, line + '\n');
        }
        return rows;
    }

    private List<Row> parseLine(String line, char separator) {
        if (line.startsWith(commentStart)) {
            return singletonList(new Comment(line.substring(commentStart.length())));
        }
        CharBuffer column = CharBuffer.allocate(line.length());
        List<String> columns = new ArrayList<>();
        boolean quoted = false;
        boolean isQuote = false;
        int i = 0;
        for (char c : line.toCharArray()) {
            if (c == separator && (!quoted || isQuote)) {
                columns.add(getValue(column));
                isQuote = false;
                quoted = false;
            } else if (c == quote) {
                if (isQuote) {
                    column.append(c);
                    isQuote = false;
                } else if (quoted) {
                    isQuote = true;
                } else if (column.position() == 0) {
                    quoted = true;
                } else if (laxMode) {
                    column.append(c);
                } else {
                    return asList(new Comment(String.format("columns with quote (%c) need to be quoted: position %d:%d, the following line was skipped", quote, lineNo.get(), i)), new Comment(line));
                }
            } else {
                column.append(c);
                isQuote = false;
            }
            ++i;
        }
        if (quoted && !isQuote) {
            return emptyList();
        }
        columns.add(getValue(column));
        return singletonList(new Columns(columns));
    }

    private String recoverFormerIncompleteLine(String line) {
        String prefix = formerLine.getAndSet(null);
        return prefix == null ? line : prefix + line;
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
                throw new IllegalStateException("Separator cannot be determined from an empty line");
            }
            separator = getBestVotedSeparator(voteForSeparators(line));
            return separator;
        }

        private Map<Character, Integer> voteForSeparators(String line) {
            Map<Character, Integer> votes = new HashMap<>();
            for (char c : possibleSeparators.toString().toCharArray()) {
                try {
                    votes.put(c, getFieldCountFromLineParsed(line, c));
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

        private int getFieldCountFromLineParsed(String line, char separator) {
            List<Row> values = RowParser.this.parseLine(line, separator);
            return values.isEmpty() ? 0 : values.get(0).getLength();
        }
    }
}
