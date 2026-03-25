package luckydrop.demo.entry.repository;

import luckydrop.demo.draw.entity.DrawEntrySummary;
import luckydrop.demo.draw.entity.DrawEntrySummaryId;
import luckydrop.demo.entry.dto.response.MyEntryResponse;
import luckydrop.demo.ticket.enums.TicketHistoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DrawEntrySummaryRepository extends JpaRepository<DrawEntrySummary, DrawEntrySummaryId> {

    interface DrawCountRow {
        Long getDrawId();
        Long getCnt();
    }

    interface RefundTarget {
        Long getUserId();
        long getEntryCount(); // 이미 사용한 티켓 수
    }

    Optional<DrawEntrySummary> findByDrawIdAndUserId(Long drawId, Long userId);

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

//    @Modifying
//    @Query(value = """
//            INSERT INTO draw_entry_summary (draw_id, user_id, entry_count)
//            VALUES (:drawId, :userId, :count)
//            ON DUPLICATE KEY UPDATE entry_count = entry_count + :count
//            """, nativeQuery = true)
//    int upsertIncrease(@Param("drawId") Long drawId,
//                       @Param("userId") Long userId,
//                       @Param("count") int count);

    @Query(value = """
            SELECT entry_count
            FROM draw_entry_summary
            WHERE draw_id = :drawId AND user_id = :userId
            """, nativeQuery = true)
    Long findEntryCount(@Param("drawId") Long drawId,
                        @Param("userId") Long userId);

    // ===== MyEntries 기능 추가 =====
    @Query(value = """
            SELECT 
                CAST(d.id AS SIGNED) as id,
                CAST(des.draw_id AS SIGNED) as drawId,
                d.title as drawTitle,
                COALESCE(ii.image_url, '') as drawImage,
                DATE_FORMAT(des.updated_at, '%Y-%m-%d %H:%i') as entryDate,
                CAST(COALESCE(SUM(tl.amount), 0) AS SIGNED) as ticketUsed,
                CAST(COALESCE(COUNT(tl.id), des.entry_count) AS SIGNED) as entryCount,
                CASE
                    WHEN d.status = 'OPEN' THEN 'OPEN'
                    WHEN d.status = 'DRAWING' THEN 'DRAWING'
                    WHEN d.status = 'COMPLETED' THEN
                        CASE WHEN dw.id IS NOT NULL THEN 'WON' ELSE 'LOST' END
                    ELSE 'DONE'
                END as status,
                DATE_FORMAT(d.end_at, '%Y-%m-%d %H:%i') as resultDate,
                CAST(CASE WHEN dw.id IS NOT NULL THEN 1 ELSE 0 END AS SIGNED) as isWinner
            FROM draw_entry_summary des
            JOIN draw d ON des.draw_id = d.id
            LEFT JOIN inventory i ON d.inventory_id = i.id
            LEFT JOIN inventory_image ii ON i.id = ii.inventory_id AND ii.sort_order = 1
            LEFT JOIN draw_winner dw ON d.id = dw.draw_id AND des.user_id = dw.user_id
            LEFT JOIN ticket_ledger tl ON tl.user_id = des.user_id 
                AND tl.type = 1          -- ✅ TicketHistoryType.USE
                AND tl.ref_type = 'DRAW' 
                AND tl.ref_id = des.draw_id
            WHERE des.user_id = :userId
              AND (:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:status IS NULL OR
                  (CASE
                      WHEN d.status = 'OPEN' THEN 'OPEN'
                      WHEN d.status = 'DRAWING' THEN 'DRAWING'
                      WHEN d.status = 'COMPLETED' THEN
                          CASE WHEN dw.id IS NOT NULL THEN 'WON' ELSE 'LOST' END
                      ELSE 'DONE'
                  END = :status))
              AND (:fromDate IS NULL OR des.created_at >= :fromDate)
              AND (:toDate IS NULL OR des.created_at <= :toDate)
            GROUP BY des.draw_id, des.user_id, d.id, d.title, ii.image_url, des.created_at, 
                     des.entry_count, d.status, dw.id
            ORDER BY des.created_at DESC
            """,
            countQuery = """
                    SELECT COUNT(DISTINCT des.draw_id)
                    FROM draw_entry_summary des
                    JOIN draw d ON des.draw_id = d.id
                    LEFT JOIN draw_winner dw ON d.id = dw.draw_id AND des.user_id = dw.user_id
                    WHERE des.user_id = :userId
                      AND (:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')))
                      AND (:status IS NULL OR
                          (CASE
                              WHEN d.status = 'OPEN' THEN 'OPEN'
                              WHEN d.status = 'DRAWING' THEN 'DRAWING'
                              WHEN d.status = 'COMPLETED' THEN
                                  CASE WHEN dw.id IS NOT NULL THEN 'WON' ELSE 'LOST' END
                              ELSE 'DONE'
                          END = :status))
                      AND (:fromDate IS NULL OR des.created_at >= :fromDate)
                      AND (:toDate IS NULL OR des.created_at <= :toDate)
                    """,
            nativeQuery = true)
    Page<MyEntryResponse> findMyEntries(
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    // 통계용 COUNT 쿼리들 - COUNT(*) 사용
    @Query(value = "SELECT COUNT(*) FROM draw_entry_summary WHERE user_id = :userId", nativeQuery = true)
    Long countTotalByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT COUNT(*) 
            FROM draw_entry_summary des 
            JOIN draw d ON des.draw_id = d.id 
            WHERE des.user_id = :userId AND d.status IN ('OPEN', 'DRAWING')
            """, nativeQuery = true)
    Long countInProgressByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT COUNT(DISTINCT des.draw_id)
            FROM draw_entry_summary des
            JOIN draw d ON des.draw_id = d.id
            JOIN draw_winner dw ON d.id = dw.draw_id AND des.user_id = dw.user_id
            WHERE des.user_id = :userId AND d.status = 'COMPLETED'
            """, nativeQuery = true)
    Long countWonByUserId(@Param("userId") Long userId);

    @Query(value = """
            SELECT COALESCE(SUM(tl.amount), 0)
            FROM ticket_ledger tl
            JOIN draw_entry_summary des ON tl.ref_id = des.draw_id
            WHERE tl.user_id = :userId 
              AND tl.type = :type 
              AND tl.ref_type = 'DRAW'
            """, nativeQuery = true)
    Long sumTicketUsedByUserId(@Param("userId") Long userId, @Param("type") TicketHistoryType type);  // TicketHistoryType -> String

    boolean existsByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end); // 오늘 당일 드로우 참여확인

    boolean existsByUserId(Long userId); // 첫 드로우 참여확인
}
