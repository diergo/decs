package diergo.csv;

public interface Row extends Iterable<String>
{
    char DEFAULT_QUOTE = '"';
    String DEFAULT_COMMENT_START = "#";

    boolean isComment();
    
    int getLength();
}
