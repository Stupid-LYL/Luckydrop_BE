package luckydrop.demo.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import luckydrop.demo.common.BaseEntity;
import luckydrop.demo.mission.enums.LimitUnit;
import luckydrop.demo.mission.enums.MissionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "mission")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Mission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", length = 20, nullable = false)
    private MissionType missionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "limit_unit", length = 20, nullable = false)
    private LimitUnit limitUnit;

    @Column(name = "limit_count", nullable = false)
    private int limitCount;

    @Column(name = "reward_ticket_amount", nullable = false)
    private int rewardTicketAmount;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    // created_at / updated_at은 DB에 존재하지만,
    // BaseEntity 컬럼명이 현재 createdAt/updatedAt로 자동매핑되어 "당장"은 완벽히 일치하지 않을 수 있음.
    // DB 컬럼은 created_at, updated_at인데 BaseEntity는 name 지정이 없어서 “완전히 동일”은 아닐 수 있음
    // 이게 싫으면 나중에 BaseEntity에 @Column(name="created_at")로 맞추는 게 정석
}
