package luckydrop.demo.ticket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketAdjustReqDto {
    private Long userId;
    private int amount;  // 양수면 증가, 음수면 감소
    private String reason;  // "보상", "오류 보정", "환불" 등
    private String adminId;  // 관리자 ID
    private String idempotencyKey;
}
