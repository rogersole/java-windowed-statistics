package rogersole.windowedstats.model;

import lombok.val;

public class Statistics {

    private final WindowSum windowSum; // to calculate sum of all elements
    private final WindowMax windowMax; // to calculate max
    private final WindowMax windowMin; // to calculate min

    public Statistics(long windowSize) {
        this.windowSum = new WindowSum(windowSize);
        this.windowMax = new WindowMax(windowSize, Transaction.amountComparatorAsc);
        this.windowMin = new WindowMax(windowSize, Transaction.amountComparatorDesc);
    }

    public void add(long now, Transaction transaction) {
        windowMax.add(now, transaction);
        windowMin.add(now, transaction);
        windowSum.add(now, transaction);
    }

    public Result getStatistics(long now) {
        val sum = windowSum.getSum(now);
        val count = windowSum.getCount(now);
        return Result.builder()
                .sum(sum)
                .avg(count > 0 ? sum/count : 0)
                .max(windowMax.get(now))
                .min(windowMin.get(now))
                .count(count).build();
    }
}
