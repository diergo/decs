package diergo.csv;

import org.junit.jupiter.api.Test;

import static diergo.csv.Rows.emptyCellToNull;
import static diergo.csv.Rows.nullToEmptyCell;
import static diergo.csv.Rows.rows;
import static diergo.csv.Rows.toStringArray;
import static diergo.csv.Rows.trimCell;
import static diergo.csv.Rows.withoutComments;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class RowsTest {

    @Test
    void commentsAreFilteredOut() {
        assertThat(withoutComments(new Comment("")), is(false));
    }

    @Test
    void rowsAreNotFilteredOut() {
        assertThat(withoutComments(new Cells()), is(true));
    }

    @Test
    void trimLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(rows(trimCell()).apply(unchanged), is(unchanged));
    }

    @Test
    void trimWorks() {
        assertThat(rows(trimCell()).apply(new Cells("hi ", "  ", " ho")), is(new Cells("hi", "", "ho")));
    }

    @Test
    void trimWorksWithNullColumns() {
        assertThat(rows(trimCell()).apply(new Cells("hi ", null, " ho")), is(new Cells("hi", null, "ho")));
    }

    @Test
    void replaceEmptyWithNullWorks() {
        assertThat(rows(emptyCellToNull()).apply(new Cells("hi", "", "ho", "")), is(new Cells("hi", null, "ho", null)));
    }

    @Test
    void replaceEmptyWithNullIgnoresNullColumns() {
        assertThat(rows(emptyCellToNull()).apply(new Cells("hi", null, "ho", "")), is(new Cells("hi", null, "ho", null)));
    }

    @Test
    void replaceEmptyWithNullLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(rows(emptyCellToNull()).apply(unchanged), is(unchanged));
    }

    @Test
    void replaceNullWithEmptyWorks() {
        assertThat(rows(nullToEmptyCell()).apply(new Cells("hi", null, "ho", null)), is(new Cells("hi", "", "ho", "")));
    }

    @Test
    void replaceNullWithEmptyLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(rows(nullToEmptyCell()).apply(unchanged), is(unchanged));
    }

    @Test
    void commentBecomesAnEmptyStringArray() {
        assertThat(toStringArray(new Comment("foo")), is(new String[0]));
    }

    @Test
    void rowBecomesAStringArrayWithCells() {
        assertThat(toStringArray(new Cells("foo", "bar")), is(new String[]{"foo", "bar"}));
    }
}
