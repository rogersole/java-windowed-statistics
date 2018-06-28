package rogersole.windowedstats.service;

import org.junit.Test;
import rogersole.windowedstats.model.Result;
import rogersole.windowedstats.model.Transaction;

import static org.junit.Assert.assertEquals;

public class StatisticsServiceTest {

    @Test
    public void windowIsWorking() throws Exception {
        StatisticsService statisticsService = new StatisticsService(500, 100);
        long now = System.currentTimeMillis();
        statisticsService.enqueueTransaction(new Transaction(now, 0));
        statisticsService.enqueueTransaction(new Transaction(now, 100));
        Thread.sleep(50);
        assertEquals(Result.builder().min(0.).max(100.).sum(100.).avg(50.).count(2).build(), statisticsService.getStatistics());
        Thread.sleep(200);
        assertEquals(Result.builder().min(0.).max(100.).sum(100.).avg(50.).count(2).build(), statisticsService.getStatistics());
        Thread.sleep(500);
        assertEquals(Result.builder().min(null).max(null).sum(0.).avg(0.).count(0).build(), statisticsService.getStatistics());
    }

    static void insertNTransactions(StatisticsService statisticsService, int numTransactions) {
        for (int i = 0; i < numTransactions; ++i) {
            statisticsService.enqueueTransaction(new Transaction(System.currentTimeMillis(), 1));
        }
    }

    @Test
    public void loadTest() throws Exception {
        StatisticsService statisticsService = new StatisticsService(10000, 10);

        final int NUM_THREADS = 120;
        final int NUM_TRANSACTIONS = 10000;
        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; ++i) {
            threads[i] = new Thread(() -> insertNTransactions(statisticsService, NUM_TRANSACTIONS));
            threads[i].run();
        }

        // Wait for the transactions to be processed
        for (int i = 0; i < NUM_THREADS; ++i)
            threads[i].join();
        while (statisticsService.getQueue().size() > 0) {
            Thread.sleep(100);
        }

        assertEquals(Result.builder().min(1.).max(1.).sum(NUM_THREADS * NUM_TRANSACTIONS * 1.).avg(1.).count(NUM_THREADS * NUM_TRANSACTIONS).build(), statisticsService.getStatistics());
    }
}
