package luckydrop.demo.draw.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DrawWinnerResponse {
    private Long drawId;
    private List<Long> winnersUserIds;
}
