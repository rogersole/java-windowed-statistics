package rogersole.windowedstats.model;

import lombok.val;

import java.math.BigDecimal;
import java.util.PriorityQueue;

public class WindowSum {

    private final long windowSize;
    private final PriorityQueue<Transaction> window;

    private BigDecimal sum = BigDecimal.ZERO; // BigDecimal avoid floating point error

    public WindowSum(long windowSize) {
        this.windowSize = windowSize;
        this.window = new PriorityQueue<>(Transaction.timestampComparator);
    }

    public void add(long now, Transaction transaction) {
        window.add(transaction);
        sum = sum.add(BigDecimal.valueOf(transaction.getAmount()));
        purgeOldItems(now);
    }

    public double getSum(long now) {
        purgeOldItems(now);
        return sum.doubleValue();
    }

    public int getCount(long now) {
        purgeOldItems(now);
        return window.size();
    }

    private void purgeOldItems(long now) {
        while (!window.isEmpty() && window.peek().getTimestamp() + windowSize < now) {
            val transaction = window.poll();
            sum = sum.subtract(BigDecimal.valueOf(transaction.getAmount()));
        }
    }
}
