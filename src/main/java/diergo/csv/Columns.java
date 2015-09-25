package diergo.csv;

import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;

class Columns implements Row {
    
    private final List<String> columns;

    Columns(String... columns) {
        this.columns = asList(columns);
    }

    Columns(List<String> columns) {
        this.columns = columns;
    }

    @Override
    public boolean isComment() {
        return false;
    }

    @Override
    public int getLength() {
        return columns.size();
    }

    @Override
    public Iterator<String> iterator() {
        return columns.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Columns other = (Columns) o;
        return columns.equals(other.columns);
    }

    @Override
    public int hashCode() {
        return columns.hashCode();
    }

    @Override
    public String toString() {
        return columns.toString();
    }
}
