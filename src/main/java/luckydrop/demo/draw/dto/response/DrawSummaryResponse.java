package luckydrop.demo.draw.dto.response;

import lombok.Builder;
import lombok.Getter;
import luckydrop.demo.draw.entity.Draw;
import java.time.LocalDateTime;

@Getter
@Builder
public class DrawSummaryResponse {

    private Long drawId;
    private String title;
    private Integer ticketCostEntry;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private boolean isBookmarked;

    private String productName;
    private String brand;
    private String imageUrl;

    private long participantCount;
    private long bookmarkCount;


    public static DrawSummaryResponse from(Draw draw, boolean isBookmarked, long bookmarkCount, long participantCount) {
        return DrawSummaryResponse.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .ticketCostEntry(draw.getTicketCostEntry())
                .startAt(draw.getStartAt())
                .endAt(draw.getEndAt())
                .isBookmarked(isBookmarked)
                .bookmarkCount(bookmarkCount)
                .participantCount(participantCount)
                .productName(draw.getInventory().getName())
                .brand(draw.getInventory().getBrand())
                .imageUrl(
                        draw.getInventory().getImages().isEmpty()
                                ? null
                                : draw.getInventory().getImages().get(0).getImageUrl()
                )
                .build();
    }
}
