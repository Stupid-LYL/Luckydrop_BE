package luckydrop.demo.draw.repository;

import luckydrop.demo.draw.entity.DrawEntrySummary;
import luckydrop.demo.draw.entity.DrawEntrySummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DrawEntrySummaryRepository extends JpaRepository<DrawEntrySummary, DrawEntrySummaryId> {

    @Query("""
         select count(s)
         from DrawEntrySummary s
         where s.drawId = :drawId
    """)
    long countParticipants(@Param("drawId") Long drawId);


    @Query("""
        select d.userId as userId, d.entryCount as entryCount
        from DrawEntrySummary d
        where d.drawId = :drawId and d.entryCount > 0
""")
    List<DrawEntrySummary.ParticipantWeight> findWeights(@Param("drawId") Long drawId);
}
