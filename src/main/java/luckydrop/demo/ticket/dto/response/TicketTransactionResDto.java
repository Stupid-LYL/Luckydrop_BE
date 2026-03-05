package luckydrop.demo.ticket.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketTransactionResDto {
    private boolean success;
    private Long userId;
    private String transactionType;
    private int amount;
    private int previousBalance;
    private int currentBalance;
    private Long ledgerId;
}
