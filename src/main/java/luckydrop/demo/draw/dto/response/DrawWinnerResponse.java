package luckydrop.demo.draw.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DrawWinnerResponse {
    private Long drawId;
    private List<WinnerItem> winners;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class WinnerItem {
        private String nickname;
        private Long usedTicketCount;
    }
}
