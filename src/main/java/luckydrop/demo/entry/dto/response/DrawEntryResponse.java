package luckydrop.demo.entry.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DrawEntryResponse {

        private Long drawId;
        private Long userId;

        private Integer spentTicketsAdded;   // 이번에 쓴 티켓
        private Integer entryTimesAdded;     // 이번 응모 횟수

        private Long spentTicketsTotal;      // 누적 티켓

}
