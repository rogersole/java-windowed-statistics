package rogersole.windowedstats.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
public class Transaction {

    public static Comparator<Transaction> timestampComparator = Comparator.comparingLong(x -> x.timestamp);
    public static Comparator<Transaction> amountComparatorAsc = Comparator.comparingDouble(x -> x.amount);
    public static Comparator<Transaction> amountComparatorDesc = amountComparatorAsc.reversed();

    private final long timestamp;
    private final double amount;

    @Override
    public String toString() {
        return "Transaction[timestamp=" + timestamp + ", amount=" + amount + ']';
    }
}
