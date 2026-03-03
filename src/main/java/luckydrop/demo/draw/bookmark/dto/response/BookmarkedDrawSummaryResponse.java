package luckydrop.demo.draw.bookmark.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 드로우 1개에 대한 정보
 * 내가 찜한 드로우에 대한 정보가 있음
 */
@Getter
@Builder
public class BookmarkedDrawSummaryResponse {
    private Long drawId;
    private String title;
    private String status;

    private LocalDateTime startAt;
    private LocalDateTime endAt;

    private long bookmarkCount;
    private long participants;

    private String thumbnailUrl;

    private boolean isBookmarked; // 항상 true
    private LocalDateTime bookmarkedAt;
}
