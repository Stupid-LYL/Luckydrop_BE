package luckydrop.demo.entry.dto.response;

import lombok.Data;

@Data
public class MyEntryStatsResponse {
    private Long total;
    private Long inProgress;
    private Long won;
    private Long totalTicketsUsed;
}
