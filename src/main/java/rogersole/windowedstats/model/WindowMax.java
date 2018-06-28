package rogersole.windowedstats.model;

import lombok.val;

import java.util.Comparator;
import java.util.TreeSet;

public class WindowMax {

    private final long windowSize;
    final Comparator<Transaction> comparator;

    // The window is ordered by timestamp but it's also sorted by amount.
    // Transactions that won't ever become maximum value are not inserted
    private final TreeSet<Transaction> window;


    public WindowMax(long windowSize, Comparator<Transaction> comparator) {
        this.windowSize = windowSize;
        this.comparator = comparator;
        this.window = new TreeSet<>(Transaction.timestampComparator);
    }

    public void add(long now, Transaction transaction) {
        if (!window.isEmpty()) {
            val subsequentTransaction = window.ceiling(transaction); // same or later timestamp
            if (subsequentTransaction != null && lessThan(transaction, subsequentTransaction)) {
                // Don't insert when the following item (future timestamp) is bigger (will keep on being max value)
            } else {
                // Delete all smaller items that are earlier than the new one
                window.headSet(transaction, true).removeIf(i -> lessThan(i, transaction));
                window.add(transaction);
            }
        } else {
            window.add(transaction);
        }
        purgeOldItems(now);
    }

    public Double get(long now) {
        purgeOldItems(now);
        // The maximum item is always kept in the head
        return window.isEmpty() ? null : window.first().getAmount();
    }

    private void purgeOldItems(long now) {
        while (!window.isEmpty() && window.first().getTimestamp() + windowSize < now) {
            window.pollFirst();
        }
    }

    private boolean lessThan(Transaction t1, Transaction t2) {
        return comparator.compare(t1, t2) < 0;
    }
}
