package diergo.csv;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

class RowParser implements Function<String, List<Row>> {

    private static final Row EMPTY_LINE = new Cells();

    final Function<String, Character> determiner;
    final char quote;
    final String commentStart;
    final boolean laxMode;
    final BiFunction<String, RuntimeException, List<Row>> errorHandler;
    private final AtomicReference<String> formerLine = new AtomicReference<>();
    private final AtomicInteger lineNo = new AtomicInteger(0);

    RowParser(CharSequence separators, char quote, String commentStart, boolean laxMode, BiFunction<String, RuntimeException, List<Row>> errorHandler) {
        this.errorHandler = errorHandler;
        this.determiner = separators.length() == 1 ? line -> separators.charAt(0) : new AutoSeparatorDeterminer(separators);
        this.quote = quote;
        this.commentStart = commentStart;
        this.laxMode = laxMode;
    }

    @Override
    public List<Row> apply(String line) {
        line = recoverFormerIncompleteLine(line);
        if (isEmpty(line)) {
            return singletonList(EMPTY_LINE);
        }
        try {
            List<Row> rows = parseLine(line, determiner.apply(line), lineNo.getAndIncrement());
            if (rows.isEmpty()) {
                formerLine.compareAndSet(null, line + '\n');
            }
            return rows;
        } catch (RuntimeException error) {
            return errorHandler.apply(line, error);
        }
    }

    private List<Row> parseLine(String line, char separator, int currentLineNo) {
        if (commentStart != null && line.startsWith(commentStart)) {
            return singletonList(new Comment(line.substring(commentStart.length())));
        }
        StringBuilder cell = new StringBuilder(line.length());
        List<String> cells = new ArrayList<>();
        boolean quoted = false;
        boolean isQuote = false;
        int length = line.length();
        for (int i = 0; i < length; i++) {
            char c = line.charAt(i);
            if (c == separator && (!quoted || isQuote)) {
                cells.add(getValue(cell));
                isQuote = false;
                quoted = false;
            } else if (c == quote) {
                if (isQuote) {
                    cell.append(c);
                    isQuote = false;
                } else if (quoted) {
                    isQuote = true;
                } else if (cell.length() == 0) {
                    quoted = true;
                } else if (laxMode) {
                    cell.append(c);
                } else {
                    throw new IllegalArgumentException(String.format("columns with quote (%c) need to be quoted: error at position %d:%d", quote, currentLineNo, i));
                }
            } else {
                cell.append(c);
                isQuote = false;
            }
        }
        if (quoted && !isQuote) {
            return emptyList();
        }
        cells.add(getValue(cell));
        return singletonList(new Cells(cells));
    }

    private String recoverFormerIncompleteLine(String line) {
        String prefix = formerLine.getAndSet(null);
        return prefix == null ? line : prefix + line;
    }

    private boolean isEmpty(String line) {
        return line == null || line.isEmpty() || line.trim().isEmpty();
    }

    private String getValue(StringBuilder value) {
        String result = value.toString();
        value.setLength(0);
        return result;
    }

    private class AutoSeparatorDeterminer implements Function<String, Character> {
        private final CharSequence possibleSeparators;
        private final AtomicInteger separator = new AtomicInteger(-1);

        private AutoSeparatorDeterminer(CharSequence possibleSeparators) {
            this.possibleSeparators = possibleSeparators;
        }

        @Override
        public Character apply(String line) {
            return Character.valueOf((char) separator.updateAndGet(
                    (int before) -> before >= 0 ? before : getBestVotedSeparator(voteForSeparators(line))
            ));
        }

        private Map<Character, Integer> voteForSeparators(String line) {
            if (isEmpty(line)) {
                throw new IllegalStateException("Separator cannot be determined from an empty line");
            }
            Map<Character, Integer> votes = new LinkedHashMap<>();
            for (char c : possibleSeparators.toString().toCharArray()) {
                votes.put(c, countCells(line, c));
            }
            return votes;
        }

        private char getBestVotedSeparator(Map<Character, Integer> votes) {
            return votes.entrySet().stream()
                    .reduce((e1, e2) -> e1.getValue() < e2.getValue() ? e2 : e1)
                    .map(Map.Entry::getKey)
                    .orElse(possibleSeparators.charAt(0));
        }

        private int countCells(String line, char separator) {
            try {
                List<Row> values = RowParser.this.parseLine(line, separator, 0);
                return values.isEmpty() ? 0 : values.get(0).getLength();
            } catch (IllegalArgumentException e) {
                return 0;
            }
        }
    }
}
