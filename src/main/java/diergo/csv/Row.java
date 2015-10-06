package diergo.csv;

import java.util.Spliterator;
import java.util.Spliterators;

import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.SIZED;
import static java.util.Spliterator.SUBSIZED;

/**
 * The internal representation of a CSV data row. The cells can be iterated.
 * Rows are created by a {@linkplain CsvParserBuilder#build() parser} from lines and
 * converted to lines by a {@linkplain CsvPrinterBuilder#build() printer}.
 */
public interface Row extends Iterable<String>
{

    /**
     * The default quote for data containing separator or multiple lines or a quote itself.
     */
    char DEFAULT_QUOTE = '"';

    /**
     * Is the row a comment?
     */
    boolean isComment();

    /**
     * The number of cells in this row. Equal to the size of the {@link Iterable}.
     * @see #iterator() 
     */
    int getLength();

    @Override
    default Spliterator<String> spliterator() {
        return Spliterators.spliterator(iterator(), getLength(), SIZED);
    }
}
