package diergo.csv;

import org.junit.Test;

import static diergo.csv.Rows.emptyCellToNull;
import static diergo.csv.Rows.nullToEmptyCell;
import static diergo.csv.Rows.rows;
import static diergo.csv.Rows.toStringArray;
import static diergo.csv.Rows.trimCell;
import static diergo.csv.Rows.withoutComments;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RowsTest {

    @Test
    public void commentsAreFilteredOut() {
        assertThat(withoutComments(new Comment("")), is(false));
    }

    @Test
    public void rowsAreNotFilteredOut() {
        assertThat(withoutComments(new Cells()), is(true));
    }

    @Test
    public void trimLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(rows(trimCell()).apply(unchanged), is(unchanged));
    }

    @Test
    public void trimWorks() {
        assertThat(rows(trimCell()).apply(new Cells("hi ", "  ", " ho")), is(new Cells("hi", "", "ho")));
    }

    @Test
    public void trimWorksWithNullColumns() {
        assertThat(rows(trimCell()).apply(new Cells("hi ", null, " ho")), is(new Cells("hi", null, "ho")));
    }

    @Test
    public void replaceEmptyWithNullWorks() {
        assertThat(rows(emptyCellToNull()).apply(new Cells("hi", "", "ho", "")), is(new Cells("hi", null, "ho", null)));
    }

    @Test
    public void replaceEmptyWithNullIgnoresNullColumns() {
        assertThat(rows(emptyCellToNull()).apply(new Cells("hi", null, "ho", "")), is(new Cells("hi", null, "ho", null)));
    }

    @Test
    public void replaceEmptyWithNullLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(rows(emptyCellToNull()).apply(unchanged), is(unchanged));
    }

    @Test
    public void replaceNullWithEmptyWorks() {
        assertThat(rows(nullToEmptyCell()).apply(new Cells("hi", null, "ho", null)), is(new Cells("hi", "", "ho", "")));
    }

    @Test
    public void replaceNullWithEmptyLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(rows(nullToEmptyCell()).apply(unchanged), is(unchanged));
    }
    
    @Test
    public void commentBecomesAnEmptyStringArray() {
        assertThat(toStringArray(new Comment("foo")), is(new String[0]));
    }

    @Test
    public void rowBecomesAStringArrayWithCells() {
        assertThat(toStringArray(new Cells("foo", "bar")), is(new String[] {"foo", "bar"}));
    }
}
