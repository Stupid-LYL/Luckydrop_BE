package luckydrop.demo.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LedgerItemResDto {
    private Long id;
    private String type;           // CHARGE / USE
    private int amount;
    private String reason;
    private String refType;
    private String refId;
    private String idempotencyKey;
    private LocalDateTime createdAt;
}
