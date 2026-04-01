package luckydrop.demo.draw.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "드로우 카드 응답")
public class DrawCardResponse {

    @Schema(description = "드로우 ID", example = "1")
    private Long drawId;

    @Schema(description = "상품명", example = "Nike Air Jordan 1")
    private String productName;

    @Schema(description = "드로우 제목", example = "에어조던 1 래플")
    private String title;

    @Schema(description = "대표 이미지 URL", example = "/uploads/sample.jpg")
    private String imageUrl;

    @Schema(description = "드로우 상태")
    private DrawStatus status;

    @Schema(description = "현재 사용자의 북마크 여부", example = "true")
    @JsonProperty("isBookmarked")
    private boolean isBookmarked;

    @Schema(description = "응모 시작 시각", example = "2026-03-29T14:00:00")
    private LocalDateTime startAt;

    @Schema(description = "응모 시작 시각", example = "2026-03-29T14:00:00")
    private LocalDateTime endAt;

    @Schema(description = "응모자 수", example = "153")
    private long participantCount;

    @Schema(description = "응모 1회당 필요한 티켓 수", example = "2")
    private Integer ticketCostEntry;

    @Schema(description = "북마크 수", example = "77")
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
