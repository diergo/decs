package diergo.csv;

public interface Row extends Iterable<String>
{
    char DEFAULT_QUOTE = '"';

    boolean isComment();
    
    int getLength();
}
