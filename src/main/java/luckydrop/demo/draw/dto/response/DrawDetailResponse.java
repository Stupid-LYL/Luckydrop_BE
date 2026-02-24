package luckydrop.demo.draw.dto.response;

import lombok.Builder;
import lombok.Getter;
import luckydrop.demo.draw.entity.Draw;

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

    private boolean isBookmark;

    private String productName;
    private String brand;
    private String productDescription;
    private Integer price;
    private List<String> images;

    public static DrawDetailResponse from(Draw draw, boolean isBookmark) {
        return DrawDetailResponse.builder()
                .drawId(draw.getId())
                .title(draw.getTitle())
                .description(draw.getDescription())
                .ticketCostEntry(draw.getTicketCostEntry())
                .winnerCount(draw.getWinnerCount())
                .startAt(draw.getStartAt())
                .endAt(draw.getEndAt())
                .isBookmark(isBookmark)
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
