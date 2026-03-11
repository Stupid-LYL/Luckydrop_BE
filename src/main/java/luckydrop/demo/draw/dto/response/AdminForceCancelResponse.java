package luckydrop.demo.draw.dto.response;

import lombok.Builder;
import lombok.Getter;
import luckydrop.demo.draw.enums.DrawStatus;

@Getter
@Builder
public class AdminForceCancelResponse {
    private Long drawId;
    private DrawStatus status;
    private boolean refunded; // 이번 호출에서 환불 수행했는지
}
