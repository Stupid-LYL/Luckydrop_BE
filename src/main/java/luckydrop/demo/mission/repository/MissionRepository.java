package luckydrop.demo.mission.repository;

import luckydrop.demo.mission.entity.Mission;
import luckydrop.demo.mission.enums.MissionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    List<Mission> findAllByActiveTrue();

    Optional<Mission> findByTypeAndActiveTrue(MissionType type);
}
