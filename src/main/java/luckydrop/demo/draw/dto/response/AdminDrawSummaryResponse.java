package luckydrop.demo.draw.dto.response;

import luckydrop.demo.draw.enums.DrawStatus;

public record AdminDrawSummaryResponse(
        Long drawId,
        String title,
        String hostNickname,
        DrawStatus status,
        long participant
) {
}
