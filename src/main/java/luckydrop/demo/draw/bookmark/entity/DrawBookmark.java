package luckydrop.demo.draw.bookmark.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.common.BaseEntity;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.user.entity.User;

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

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @MapsId("drawId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "draw_id", nullable = false)
    private Draw draw;

    public static DrawBookmark of(User user, Draw draw) {
        return DrawBookmark.builder()
                .id(new DrawBookmarkId(user.getId(), draw.getId()))
                .user(user)
                .draw(draw)
                .build();
    }

    public Long getUserId() {
        return id.getUserId();
    }

    public Long getDrawId() {
        return id.getDrawId();
    }
}
