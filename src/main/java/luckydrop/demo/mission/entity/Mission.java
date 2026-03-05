package luckydrop.demo.mission.entity;

import jakarta.persistence.*;
import lombok.*;
import luckydrop.demo.common.BaseEntity;
import luckydrop.demo.mission.enums.MissionType;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Mission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int rewardAmount; // 기본 보상(티켓)

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
