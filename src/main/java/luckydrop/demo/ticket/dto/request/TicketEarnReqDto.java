package luckydrop.demo.ticket.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketEarnReqDto {
    private Long userId;
    private int amount;
    private String reason;  // "출석체크", "이벤트 참여", "회원가입" 등
    private String refType;  // "ATTENDANCE", "EVENT", "SIGNUP" 등
    private String refId;  // 참조 ID (이벤트 ID 등)
    private String idempotencyKey;  // 중복 방지 키
}
