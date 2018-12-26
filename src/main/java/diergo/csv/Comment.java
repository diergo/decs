package diergo.csv;

import java.util.Collections;
import java.util.Iterator;

class Comment implements Row {

    private final String comment;

    Comment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean isComment() {
        return true;
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.singleton(comment).iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Comment other = (Comment) obj;
        return comment.equals(other.comment);
    }

    @Override
    public int hashCode() {
        return comment.hashCode();
    }

    @Override
    public String toString() {
        return "#" + comment;
    }
}
