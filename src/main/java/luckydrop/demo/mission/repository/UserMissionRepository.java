package luckydrop.demo.mission.repository;

import luckydrop.demo.mission.entity.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    boolean existsByUserIdAndMissionIdAndPeriodKey(Long userId, Long missionId, String periodKey);

    Optional<UserMission> findByUserIdAndMissionIdAndPeriodKey(Long userId, Long missionId, String periodKey);

    Optional<UserMission> findTopByUserIdAndMissionIdOrderByPeriodKeyDesc(Long userId, Long missionId);
}
