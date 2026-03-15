package luckydrop.demo.draw.dto.response;

import lombok.Builder;
import lombok.Getter;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DrawDetailResponse {

    private Long drawId;
    private String title;
    private String description;
    private Integer ticketCostEntry;
    private Integer winnerCount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private DrawStatus status;

    private boolean isBookmarked;
    private long participantCount;
    private long bookmarkCount;

    private String productName;
    private String brand;
    private String productDescription;
    private Integer price;
    private List<String> images;

    private Integer myTicketBalance;

    public static DrawDetailResponse from(
            Draw draw,
            boolean isBookmarked,
            long bookmarkCount,
            long participantCount,
            Integer myTicketBalance
    ) {
        return DrawDetailResponse.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .description(draw.getDescription())
                .ticketCostEntry(draw.getTicketCostEntry())
                .winnerCount(draw.getWinnerCount())
                .startAt(draw.getStartAt())
                .endAt(draw.getEndAt())
                .status(draw.getStatus())
                .isBookmarked(isBookmarked)
                .bookmarkCount(bookmarkCount)
                .participantCount(participantCount)
                .myTicketBalance(myTicketBalance)
                .productName(draw.getInventory().getName())
                .brand(draw.getInventory().getBrand())
                .productDescription(draw.getInventory().getDescription())
                .price(draw.getInventory().getRetailPrice())
                .images(
                        draw.getInventory().getImages()
                                .stream()
                                .map(img -> img.getImageUrl())
                                .toList()
                )
                .build();
    }

}
