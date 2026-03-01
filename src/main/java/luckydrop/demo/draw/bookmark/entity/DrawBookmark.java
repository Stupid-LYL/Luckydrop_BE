package luckydrop.demo.draw.bookmark.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.common.BaseEntity;

@Slf4j
@Entity
@Table(
        name = "draw_bookmark",
        indexes = {
                @Index(name = "idx_draw_bookmark_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_draw_bookmark_draw", columnList = "draw_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DrawBookmark extends BaseEntity {

    @EmbeddedId
    private DrawBookmarkId id;

    public static DrawBookmark of(Long userId, Long drawId) {
        return DrawBookmark.builder()
                .id(new DrawBookmarkId(userId, drawId))
                .build();
    }

    public Long getUserId() {
        return id.getUserId();
    }

    public Long getDrawId() {
        return id.getDrawId();
    }
}
