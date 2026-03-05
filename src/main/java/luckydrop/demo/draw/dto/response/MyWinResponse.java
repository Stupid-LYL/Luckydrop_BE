package luckydrop.demo.draw.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyWinResponse {
    private Long drawId;
    private LocalDateTime wonAt;
}
