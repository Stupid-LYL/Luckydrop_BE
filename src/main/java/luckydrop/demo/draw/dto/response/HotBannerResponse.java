package luckydrop.demo.draw.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class HotBannerResponse {

    private Long drawId;
    private String reason;

    private String title;
    private String productName;

    private List<String> images;

    private Boolean isBookmarked;

    private Integer ticketCostEntry;
    private LocalDateTime endAt;
    private LocalDateTime startAt;
}