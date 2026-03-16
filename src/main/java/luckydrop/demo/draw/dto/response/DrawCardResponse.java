package luckydrop.demo.draw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawStatus;

import java.time.LocalDateTime;

@Getter
@Builder
public class DrawCardResponse {

    private Long drawId;

    private String productName;
    private String title;
    private String imageUrl;

    private DrawStatus status;

    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private long participantCount;
    private Integer ticketCostEntry;
    private long bookmarkCount;


    public static DrawCardResponse from(
            Draw draw,
            boolean isBookmarked,
            long bookmarkCount,
            long participantCount) {
        return DrawCardResponse.builder()
                .drawId(draw.getId())
                .productName(draw.getInventory().getName())
                .title(draw.getTitle())
                .ticketCostEntry(draw.getTicketCostEntry())
                .status(draw.getStatus())

                .startAt(draw.getStartAt())
                .endAt(draw.getEndAt())

                .isBookmarked(isBookmarked)
                .bookmarkCount(bookmarkCount)
                .participantCount(participantCount)

                .imageUrl(
                        draw.getInventory().getImages().isEmpty()
                                ? null
                                : draw.getInventory().getImages().get(0).getImageUrl()
                )
                .build();
    }
}
