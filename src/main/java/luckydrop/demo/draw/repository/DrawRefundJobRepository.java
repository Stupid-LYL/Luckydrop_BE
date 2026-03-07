package luckydrop.demo.draw.repository;

import jakarta.persistence.LockModeType;
import luckydrop.demo.draw.entity.DrawRefundJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DrawRefundJobRepository extends JpaRepository<DrawRefundJob, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select j from DrawRefundJob j where j.draw.id = :drawId")
    Optional<DrawRefundJob> findByDrawIdForUpdate(@Param("drawId") Long drawId);
}
