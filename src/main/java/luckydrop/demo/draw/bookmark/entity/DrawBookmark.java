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
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_draw_bookmark_user_draw", columnNames = {"user_id", "draw_id"})
        },
        indexes = {
                @Index(name = "idx_draw_bookmark_user_created", columnList = "user_id, created_at"),
                @Index(name = "idx_draw_bookmark_draw", columnList = "draw_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DrawBookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "draw_id", nullable = false)
    private Long drawId;

    public static DrawBookmark of(Long userId, Long drawId) {
        return DrawBookmark.builder()
                .userId(userId)
                .drawId(drawId)
                .build();
    }
}
