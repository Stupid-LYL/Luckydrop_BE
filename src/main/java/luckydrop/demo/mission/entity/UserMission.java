package luckydrop.demo.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import luckydrop.demo.common.BaseEntity;
import luckydrop.demo.mission.enums.UserMissionStatus;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(
        name = "user_mission",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_mission_unique",
                        columnNames = {"user_id", "mission_id", "period_key"}
                )
        }
)
public class UserMission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user 엔티티를 직접 물고 가기보단, 지금 단계에서는 userId만 들고 가는 게 단순함
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "mission_id", nullable = false)
    private Long missionId;

    // yyyyMMdd 형태 추천
    @Column(name = "period_key", nullable = false, length = 8)
    private String periodKey; // "20260224" 같은 문자열 날짜 키

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserMissionStatus status;

    // 연속 출석용(출첵 미션에만 의미 있음)
    @Column(nullable = false)
    private int streak; // 오늘 출첵 처리 후의 연속 출석 일수
}
