package rogersole.windowedstats.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class WindowMaxTest {

    WindowMax windowMax;

    @Before
    public void setUp() {
        windowMax = new WindowMax(1000, Transaction.amountComparatorAsc);
    }

    @Test
    public void nullWhenNoTransactions() {
        windowMax.add(0, new Transaction(0, 42));
        assertNull(windowMax.get(2000));
    }

    @Test
    public void oneTransaction() {
        windowMax.add(0, new Transaction(0, 42));
        assertEquals(42, windowMax.get(500), 0);
    }

    @Test
    public void sameTimestamp() {
        windowMax.add(0, new Transaction(0, 50));
        windowMax.add(0, new Transaction(0, 70));
        windowMax.add(0, new Transaction(0, 30));
        windowMax.add(0, new Transaction(0, 40));
        assertEquals(70, windowMax.get(500), 0);
    }

    @Test
    public void discardOldTransactions() {
        windowMax.add(0, new Transaction(0, 200));
        windowMax.add(0, new Transaction(100, 10));
        windowMax.add(0, new Transaction(200, 100));
        assertEquals(200, windowMax.get(1000), 0);
        assertEquals(100, windowMax.get(1150), 0);
    }

    @Test
    public void newerSameAmount() {
        windowMax.add(0, new Transaction(0, 100));
        windowMax.add(0, new Transaction(100, 100));
        assertEquals(100, windowMax.get(1050), 0);
    }

    @Test
    public void ascending() {
        windowMax.add(0, new Transaction(0, 0));
        windowMax.add(0, new Transaction(100, 100));
        assertEquals(100, windowMax.get(1000), 0);
        assertEquals(100, windowMax.get(1050), 0);
        assertNull(windowMax.get(1150));
    }

    @Test
    public void descending() throws Exception {
        windowMax.add(0, new Transaction(0, 100));
        windowMax.add(0, new Transaction(100, 0));
        assertEquals(100, windowMax.get(1000), 0);
        assertEquals(0, windowMax.get(1050), 0);
        assertNull(windowMax.get(1150));
    }

    @Test
    public void mixed() {
        windowMax.add(0, new Transaction(0, 200));
        windowMax.add(0, new Transaction(100, 0));
        windowMax.add(0, new Transaction(200, 100));
        assertEquals(200, windowMax.get(1000), 0);
        assertEquals(100, windowMax.get(1050), 0);
        assertEquals(100, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeEarliestBiggest() {
        windowMax.add(0, new Transaction(100, 100));
        windowMax.add(0, new Transaction(200, 10));
        windowMax.add(0, new Transaction(0, 200));
        assertEquals(200, windowMax.get(1000), 0);
        assertEquals(100, windowMax.get(1050), 0);
        assertEquals(10, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeEarliestMedium() {
        windowMax.add(0, new Transaction(100, 100));
        windowMax.add(0, new Transaction(200, 10));
        windowMax.add(0, new Transaction(0, 50));
        assertEquals(100, windowMax.get(1000), 0);
        assertEquals(100, windowMax.get(1050), 0);
        assertEquals(10, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeEarliestSmallest() {
        windowMax.add(0, new Transaction(100, 100));
        windowMax.add(0, new Transaction(200, 10));
        windowMax.add(0, new Transaction(0, 0));
        assertEquals(100, windowMax.get(1000), 0);
        assertEquals(100, windowMax.get(1050), 0);
        assertEquals(10, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeMiddleBiggest() {
        windowMax.add(0, new Transaction(0, 100));
        windowMax.add(0, new Transaction(200, 10));
        windowMax.add(0, new Transaction(100, 200));
        assertEquals(200, windowMax.get(1000), 0);
        assertEquals(200, windowMax.get(1050), 0);
        assertEquals(10, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeMiddleMedium() {
        windowMax.add(0, new Transaction(0, 100));
        windowMax.add(0, new Transaction(200, 10));
        windowMax.add(0, new Transaction(100, 50));
        assertEquals(100, windowMax.get(1000), 0);
        assertEquals(50, windowMax.get(1050), 0);
        assertEquals(10, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeMiddleSmallest() {
        windowMax.add(0, new Transaction(0, 100));
        windowMax.add(0, new Transaction(200, 10));
        windowMax.add(0, new Transaction(100, 0));
        assertEquals(100, windowMax.get(1000), 0);
        assertEquals(10, windowMax.get(1050), 0);
        assertEquals(10, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeLatestBiggest() {
        windowMax.add(0, new Transaction(0, 100));
        windowMax.add(0, new Transaction(100, 10));
        windowMax.add(0, new Transaction(200, 200));
        assertEquals(200, windowMax.get(1000), 0);
        assertEquals(200, windowMax.get(1050), 0);
        assertEquals(200, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeLatestMedium() {
        windowMax.add(0, new Transaction(0, 100));
        windowMax.add(0, new Transaction(100, 10));
        windowMax.add(0, new Transaction(200, 50));
        assertEquals(100, windowMax.get(1000), 0);
        assertEquals(50, windowMax.get(1050), 0);
        assertEquals(50, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }

    @Test
    public void mixedTimeLatestSmallest() {
        windowMax.add(0, new Transaction(0, 100));
        windowMax.add(0, new Transaction(100, 10));
        windowMax.add(0, new Transaction(200, 0));
        assertEquals(100, windowMax.get(1000), 0);
        assertEquals(10, windowMax.get(1050), 0);
        assertEquals(0, windowMax.get(1150), 0);
        assertNull(windowMax.get(1250));
    }
}
