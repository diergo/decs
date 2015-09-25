package diergo.csv;

public interface Row extends Iterable<String>
{
    boolean isComment();
    
    int getLength();
}
