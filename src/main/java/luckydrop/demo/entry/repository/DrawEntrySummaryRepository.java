package luckydrop.demo.entry.repository;

import luckydrop.demo.draw.entity.DrawEntrySummary;
import luckydrop.demo.draw.entity.DrawEntrySummaryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DrawEntrySummaryRepository extends JpaRepository<DrawEntrySummary, DrawEntrySummaryId> {

    interface DrawCountRow {
        Long getDrawId();
        Long getCnt();
    }

    interface RefundTarget {
        Long getUserId();
        long getEntryCount(); // 이미 사용한 티켓 수
    }

    @Query("""
        select s.userId as userId, s.entryCount as entryCount
        from DrawEntrySummary s
        where s.drawId = :drawId
    """)
    List<RefundTarget> findRefundTargets(@Param("drawId") Long drawId);


    @Query("""
        select s.drawId as drawId, count(s.userId) as cnt
        from DrawEntrySummary s
        where s.drawId in :drawIds
        group by s.drawId
""")
    List<DrawCountRow> countParticipantsByDrawIds(@Param("drawIds") List<Long> drawIds);


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

    @Modifying
    @Query(value = """
            INSERT INTO draw_entry_summary (draw_id, user_id, entry_count)
            VALUES (:drawId, :userId, :count)
            ON DUPLICATE KEY UPDATE entry_count = entry_count + :count
            """, nativeQuery = true)
    int upsertIncrease(@Param("drawId") Long drawId,
                       @Param("userId") Long userId,
                       @Param("count") int count);

    @Query(value = """
            SELECT entry_count
            FROM draw_entry_summary
            WHERE draw_id = :drawId AND user_id = :userId
            """, nativeQuery = true)
    Long findEntryCount(@Param("drawId") Long drawId,
                        @Param("userId") Long userId);
}
