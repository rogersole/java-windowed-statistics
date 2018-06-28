package rogersole.windowedstats.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WindowSumTest {

    WindowSum windowSum;

    @Before
    public void setUp() {
        windowSum = new WindowSum(1000);
    }

    @Test
    public void shouldReturnZeroIfEmpty() {
        assertEquals(0, windowSum.getSum(0), 0);
        assertEquals(0, windowSum.getCount(0));
    }

    @Test
    public void shouldReturnSum() {
        windowSum.add(0, new Transaction(0, 100));
        windowSum.add(0, new Transaction(0, 100));
        assertEquals(200, windowSum.getSum(500), 0);
        assertEquals(2, windowSum.getCount(500));
    }

    @Test
    public void shouldDropOldTransactions() {
        windowSum.add(0, new Transaction(0, 100));
        windowSum.add(0, new Transaction(0, 100));
        windowSum.add(0, new Transaction(1000, 100));
        windowSum.add(0, new Transaction(1000, 100));
        // First two added items should be dropped
        assertEquals(200, windowSum.getSum(1500), 0);
        assertEquals(2, windowSum.getCount(1500));
    }

    @Test
    public void shouldNotAccumulateFloatingPointError() {
        windowSum.add(0, new Transaction(0, 10.4));
        windowSum.add(50, new Transaction(50, 10.4));
        windowSum.add(500, new Transaction(500, 100));
        assertEquals(100, windowSum.getSum(1200), 0);
    }
}
