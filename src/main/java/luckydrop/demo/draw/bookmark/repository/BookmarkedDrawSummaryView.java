package luckydrop.demo.draw.bookmark.repository;

import java.sql.Timestamp;

public interface BookmarkedDrawSummaryView {
    Long getDrawId();
    String getTitle();
    String getStatus();

    Timestamp getStartAt(); // draw.start_at
    Timestamp getEndAt(); // draw.end_at

    Long getBookmarkCount();
    Long getParticipants(); // 참여자 수

    String getThumbnailUrl(); // inventory_images.image_url

    Timestamp getBookmarkedAt(); // draw_bookmark.created_at
}
