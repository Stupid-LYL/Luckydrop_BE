package luckydrop.demo.ticket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketUseReqDto {
    private Long userId;
    private int amount;
    private String reason;  // "응모", "상품 교환" 등
    private String refType;  // "DRAW", "EXCHANGE" 등
    private String refId;  // 응모 ID, 상품 ID 등
    private String idempotencyKey;
}
