package diergo.csv;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class RowsTest {

    @Test
    public void trimLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(Rows.trim(unchanged), is(unchanged));
    }

    @Test
    public void trimWorks() {
        assertThat(Rows.trim(new Columns("hi ", "  ", " ho")), is(new Columns("hi", "", "ho")));
    }

    @Test
    public void trimWorksWithNullColumns() {
        assertThat(Rows.trim(new Columns("hi ", null, " ho")), is(new Columns("hi", null, "ho")));
    }

    @Test
    public void replaceEmptyWithNullWorks() {
        assertThat(Rows.replaceEmptyWithNull(new Columns("hi", "", "ho", "")), is(new Columns("hi", null, "ho", null)));
    }

    @Test
    public void replaceEmptyWithNullIgnoresNullColumns() {
        assertThat(Rows.replaceEmptyWithNull(new Columns("hi", null, "ho", "")), is(new Columns("hi", null, "ho", null)));
    }

    @Test
    public void replaceEmptyWithNullLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(Rows.replaceEmptyWithNull(unchanged), is(unchanged));
    }

    @Test
    public void replaceNullWithEmptyWorks() {
        assertThat(Rows.replaceNullWithEmpty(new Columns("hi", null, "ho", null)), is(new Columns("hi", "", "ho", "")));
    }

    @Test
    public void replaceNullWithEmptyLeavesCommentsUnchanged() {
        Comment unchanged = new Comment("");
        assertThat(Rows.replaceNullWithEmpty(unchanged), is(unchanged));
    }
}
