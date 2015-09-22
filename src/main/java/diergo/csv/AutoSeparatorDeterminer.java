package diergo.csv;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Determines the separator for the fields of a CSV line by parsing the first
 * line.
 *
 * @since 1.1
 */
class AutoSeparatorDeterminer
        implements SeparatorDeterminer {

    private final CharSequence possibleSeparators;
    private Character separator = null;

    public AutoSeparatorDeterminer(CharSequence possibleSeparators) {
        if (possibleSeparators == null || possibleSeparators.length() == 0) {
            throw new IllegalArgumentException("Possible separators must not be empty");
        }
        this.possibleSeparators = possibleSeparators;
    }

    /**
     * Determines the separator from the first non empty line passed.
     *
     * @throws IllegalStateException if the line is empty and no separator has been determined before
     */
    public char determineSeparator(String line) {
        if (separator != null) {
            return separator;
        }
        if (line == null || line.trim().length() == 0) {
            throw new IllegalStateException("Separator not determined");
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
        return new CsvReaderStream(EnumSet.noneOf(Option.class), anyLine -> separator)
                .parseLine(line).length;
    }
}
