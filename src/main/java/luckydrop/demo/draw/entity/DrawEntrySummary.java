package luckydrop.demo.draw.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import luckydrop.demo.common.BaseEntity;
import luckydrop.demo.user.entity.User;

@Entity
@Table
@IdClass(DrawEntrySummaryId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DrawEntrySummary extends BaseEntity {

    @Id
    @Column(name = "draw_id", nullable = false)
    private Long drawId;

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "entry_count", nullable = false)
    private Long entryCount;

    // 연관관계 명시용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "draw_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_draw_entry_summary_draw")
    )
    private Draw draw;

    // 연관관계 명시용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(name = "fk_draw_entry_summary_user")
    )
    private User user;


    public interface ParticipantWeight {
        Long getUserId();
        Long getEntryCount();
    }
}
