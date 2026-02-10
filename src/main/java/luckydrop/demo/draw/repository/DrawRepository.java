package luckydrop.demo.draw.repository;

import jakarta.persistence.LockModeType;
import luckydrop.demo.draw.entity.Draw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DrawRepository extends JpaRepository<Draw, Long> {
    boolean existsByInventoryId(Long inventoryId); // inventory_id UNIQUE 체크용

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Draw d where d.id = :id")
    Optional<Draw> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query("""
        update Draw d
        set d.status = 'ACTIVE'
        where d.status = 'DRAFT'
          and d.startAt <= :now
    """)
    int updateDraftToActive(@Param("now")LocalDateTime now);

    @Modifying
    @Query("""
        update Draw d
        set d.status = 'DRAWING'
        where d.status = 'ACTIVE'
            and d.endAt <= :now
    """)
    int updateActiveToDrawing(@Param("now") LocalDateTime now);
}



