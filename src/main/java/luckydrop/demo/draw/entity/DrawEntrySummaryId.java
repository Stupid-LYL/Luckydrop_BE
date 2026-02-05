package luckydrop.demo.draw.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class DrawEntrySummaryId implements Serializable {

    private Long drawId;
    private Long userId;
}
