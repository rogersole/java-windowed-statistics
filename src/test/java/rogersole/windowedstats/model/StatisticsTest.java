package rogersole.windowedstats.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StatisticsTest {

    Statistics statistics;

    @Before
    public void setUp() {
        statistics = new Statistics(1000);
    }

    @Test
    public void worksForEmpty() {
        statistics.add(0, new Transaction(0, 100));
        Result result = statistics.getStatistics(1500);
        assertNull(result.min);
        assertNull(result.max);
        assertEquals(0, result.avg, 0);
        assertEquals(0, result.sum, 0);
        assertEquals(0, result.count, 0);
    }

    @Test
    public void worksInGeneral() {
        statistics.add(0, new Transaction(0, 100));
        statistics.add(1000, new Transaction(0, 1000));
        statistics.add(1000, new Transaction(1000, 100));
        Result result = statistics.getStatistics(1500);
        assertEquals(100, result.min, 0);
        assertEquals(100, result.max, 0);
        assertEquals(100, result.avg, 0);
        assertEquals(100, result.sum, 0);
        assertEquals(1, result.count, 0);
    }
}
