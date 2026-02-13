package luckydrop.demo.entry.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DrawEntryResponse {

        private Long drawId;
        private Long userId;
        private Integer addedCount;
        private Long totalCount;
}
