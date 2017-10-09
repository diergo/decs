package diergo.csv;

import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;

class Cells implements Row {
    
    private final List<String> cells;

    Cells(String... cells) {
        this(asList(cells));
    }

    Cells(List<String> cells) {
        this.cells = cells;
    }

    @Override
    public boolean isComment() {
        return false;
    }

    @Override
    public int getLength() {
        return cells.size();
    }

    @Override
    public Iterator<String> iterator() {
        return cells.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Cells other = (Cells) obj;
        return cells.equals(other.cells);
    }

    @Override
    public int hashCode() {
        return cells.hashCode();
    }

    @Override
    public String toString() {
        return cells.toString();
    }
}
