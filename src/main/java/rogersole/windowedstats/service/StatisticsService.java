package rogersole.windowedstats.service;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import rogersole.windowedstats.model.Result;
import rogersole.windowedstats.model.Statistics;
import rogersole.windowedstats.model.Transaction;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class StatisticsService {

    @Getter
    private final BlockingQueue<Transaction> queue = new LinkedBlockingQueue<>();

    private final long windowCleaningIntervalInMillis;
    private final Statistics statistics;
    private volatile Result latestResult;

    public StatisticsService(long windowSizeInMillis, long windowCleaningIntervalInMillis) {
        log.info("Starting StatisticsServer - windowSizeInMillis: {}, windowCleaningIntervalInMillis: {}",
                windowSizeInMillis, windowCleaningIntervalInMillis);
        this.windowCleaningIntervalInMillis = windowCleaningIntervalInMillis;
        this.statistics = new Statistics(windowSizeInMillis);

        initTransactionConsumer();
        initIntervalCleaner();
    }

    private void initIntervalCleaner() {
        new Thread(new IntervalStatsCleaner()).start();
    }

    private void initTransactionConsumer() {
        new Thread(new TransactionConsumer()).start();
    }

    public Result getStatistics() {

        // Strong consistent and almost O(1) solution
        // Since the latestResult is calculated, the old transactions are purged almost up to date
        synchronized (statistics) {
            val now = System.currentTimeMillis();
            return statistics.getStatistics(now);
        }

        // Eventually consistent and strong O(1) solution
        // comment the previous code and uncomment the following. Use the latest update value
        // return latestResult;
    }

    public void enqueueTransaction(Transaction transaction) {
        queue.add(transaction);
    }

    private class TransactionConsumer implements Runnable {
        public void run() {
            log.info("TransactionConsumer started");
            while(true) {
                try {
                    val transaction = queue.take();
                    log.info("TransactionConsumer: received a transaction: {}", transaction);
                    synchronized (statistics) {
                        val now = System.currentTimeMillis();
                        statistics.add(now, transaction);
                        // Immediately refresh
                        latestResult = statistics.getStatistics(now);
                    }
                } catch (InterruptedException e) {
                    log.error("TransactionConsumer: error while getting element form the queue", e);
                }
            }
        }
    }

    private class IntervalStatsCleaner implements Runnable {
        public void run() {
            log.info("IntervalStatsCleaner started");
            while(true) {
                try {
                    Thread.sleep(windowCleaningIntervalInMillis);
                    synchronized (statistics) {
                        val now = System.currentTimeMillis();
                        latestResult = statistics.getStatistics(now);
                    }
                    log.debug("IntervalStatsCleaner: refreshed the latest result: {}", latestResult);
                } catch (Exception e) {
                    log.error("IntervalStatsCleaner: error while cleaning ", e);
                }
            }
        }
    }
}
