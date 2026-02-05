package luckydrop.demo.draw.repository;

import luckydrop.demo.draw.entity.DrawEntrySummary;
import luckydrop.demo.draw.entity.DrawEntrySummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DrawEntrySummaryRepository extends JpaRepository<DrawEntrySummary, DrawEntrySummaryId> {

    @Query("""
         select count(s)
         from DrawEntrySummary s
         where s.drawId = :drawId
    """)
    long countParticipants(@Param("drawId") Long drawId);
}
