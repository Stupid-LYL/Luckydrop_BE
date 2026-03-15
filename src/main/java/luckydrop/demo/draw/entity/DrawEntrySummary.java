package luckydrop.demo.draw.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import luckydrop.demo.common.BaseEntity;

@Entity
@Table
@IdClass(DrawEntrySummaryId.class)
@Getter
@NoArgsConstructor
public class DrawEntrySummary extends BaseEntity {

    @Id
    @Column(name = "draw_id", nullable = false)
    private Long drawId;

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "entry_count", nullable = false)
    private Long entryCount;

    // 임시 메서드
    public static DrawEntrySummary create(Long drawId, Long userId) {
        DrawEntrySummary summary = new DrawEntrySummary();
        summary.drawId = drawId;
        summary.userId = userId;
        summary.entryCount = 0L;
        return summary;
    }

    public void increaseEntryCount() {
        this.entryCount++;
    }

    public interface ParticipantWeight {
        Long getUserId();
        Long getEntryCount();
    }
}
