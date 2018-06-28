package rogersole.windowedstats.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class Result {
    final Double min;
    final Double max;
    final Double sum;
    final Double avg;
    final int count;
}


