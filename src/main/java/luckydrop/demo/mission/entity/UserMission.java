package luckydrop.demo.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import luckydrop.demo.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_mission",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_mission_period",
                        columnNames = {"user_id", "mission_id", "period_key"}
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserMission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "mission_id", nullable = false)
    private Long missionId;

    @Column(name = "period_key", length = 20, nullable = false)
    private String periodKey;

    @Column(name = "progress_count", nullable = false)
    private int progressCount;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "rewarded_at")
    private LocalDateTime rewardedAt;

    // ---- domain update methods (dirty checking용) ----

    public void setPeriodKey(String periodKey) {
        this.periodKey = periodKey; //nullable = false라서, 예시로 넣었던 periodKey(null) 생성 코드는 DB에서 터짐
    }

    public void setProgressCount(int progressCount) {
        this.progressCount = progressCount;
    }

    public void markCompletedNow() {
        this.completedAt = LocalDateTime.now();
    }

    public void markRewardedNow() {
        this.rewardedAt = LocalDateTime.now();
    }

    public void clearRewardedAt() {
        this.rewardedAt = null;
    }

}
