package luckydrop.demo.draw.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HotBannerResponse {
    private Long drawId;
    private String reason;
}
