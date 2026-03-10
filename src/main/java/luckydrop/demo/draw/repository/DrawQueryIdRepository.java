package luckydrop.demo.draw.repository;

import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface DrawQueryIdRepository extends JpaRepository<Draw, Long> {

    //전체보기
    @Query("""
        select d.id
        from Draw d
        where d.status <> :excludedStatus
        order by d.createdAt desc
    """)
    Page<Long> findAllLatestIds(@Param("excludedStatus") DrawStatus excludedStatus,
                                Pageable pageable);

    // UPCOMING status = DRAFT AND startAt > now
    @Query("""
        select d.id
        from Draw d
        where d.status = :draft
            and d.startAt > :now
        order by d.createdAt desc
""")
    Page<Long> findUpcomingLatestIds(@Param("draft") DrawStatus draft,
                                     @Param("now")LocalDateTime now,
                                     Pageable pageable);

    // UPCOMING 찜 많은 순
    @Query("""
        select d.id
        from Draw d
        where d.status = :draft
            and d.startAt > :now
        order by
          (select count(b) from DrawBookmark b
            where b.id.drawId = d.id) desc,
        d.createdAt desc
""")
    Page<Long> findUpcomingBookmarkIds(@Param("draft") DrawStatus draft,
                                       @Param("now") LocalDateTime now,
                                       Pageable pageable);


    // ONGOING: (status = ACTIVE OR DRAWING) AND startAt <= now AND endAt > now
    @Query("""
        select d.id
        from Draw d
        where (d.status = :active or d.status = :drawing)
          and d.startAt <= :now
          and d.endAt > :now
        order by d.startAt desc
    """)
    Page<Long> findOngoingStartedDescIds(@Param("active") DrawStatus active,
                                         @Param("drawing") DrawStatus drawing,
                                         @Param("now") LocalDateTime now,
                                         Pageable pageable);


    // ONGOING 응모자 많은 순 (탭 정책): participantCount desc, tie startAt desc
    @Query("""
        select d.id
        from Draw d
        where (d.status = :active or d.status = :drawing)
          and d.startAt <= :now
          and d.endAt > :now
        order by
          (select count(s) from DrawEntrySummary s where s.drawId = d.id) desc,
          d.startAt desc
    """)
    Page<Long> findOngoingParticipantIds(@Param("active") DrawStatus active,
                                         @Param("drawing") DrawStatus drawing,
                                         @Param("now") LocalDateTime now,
                                         Pageable pageable);


    // ONGOING 찜 많은 순 (탭 정책): bookmarkCount desc, tie startAt desc
    @Query("""
        select d.id
        from Draw d
        where (d.status = :active or d.status = :drawing)
          and d.startAt <= :now
          and d.endAt > :now
        order by
          (select count(b) from luckydrop.demo.draw.bookmark.entity.DrawBookmark b
            where b.id.drawId = d.id) desc,
          d.startAt desc
    """)
    Page<Long> findOngoingBookmarkIds(@Param("active") DrawStatus active,
                                      @Param("drawing") DrawStatus drawing,
                                      @Param("now") LocalDateTime now,
                                      Pageable pageable);



    // ONGOING 마감 임박 순 (탭 정책): endAt asc, tie startAt desc
    @Query("""
        select d.id
        from Draw d
        where (d.status = :active or d.status = :drawing)
          and d.startAt <= :now
          and d.endAt > :now
        order by d.endAt asc, d.startAt desc
    """)
    Page<Long> findOngoingEndingSoonIds(@Param("active") DrawStatus active,
                                        @Param("drawing") DrawStatus drawing,
                                        @Param("now") LocalDateTime now,
                                        Pageable pageable);


    // CLOSED: status = CLOSE OR endAt <= now
    // CLOSED 정렬(고정): endAt desc
    @Query("""
        select d.id
        from Draw d
        where d.status = :close
           or d.endAt <= :now
        order by d.endAt desc
    """)
    Page<Long> findClosedEndedDescIds(@Param("close") DrawStatus close,
                                      @Param("now") LocalDateTime now,
                                      Pageable pageable);


    // 1순위: participantCount desc, tie endAt asc -> createdAt desc (ONGOING 대상으로 의미있음)
    @Query("""
        select d.id
        from Draw d
        where (d.status = :active or d.status = :drawing)
          and d.startAt <= :now
          and d.endAt > :now
        order by
          (select count(s) from DrawEntrySummary s where s.drawId = d.id) desc,
          d.endAt asc,
          d.createdAt desc
    """)
    Page<Long> findHot1PopularOngoingIds(@Param("active") DrawStatus active,
                                         @Param("drawing") DrawStatus drawing,
                                         @Param("now") LocalDateTime now,
                                         Pageable pageable);


    // 2순위 fallback: UPCOMING 중 bookmarkCount desc, tie startAt asc -> createdAt desc
    @Query("""
        select d.id
        from Draw d
        where d.status = :draft
          and d.startAt > :now
        order by
          (select count(b) from luckydrop.demo.draw.bookmark.entity.DrawBookmark b
            where b.id.drawId = d.id) desc,
          d.startAt asc,
          d.createdAt desc
    """)
    Page<Long> findHot2UpcomingBookmarkIds(@Param("draft") DrawStatus draft,
                                           @Param("now") LocalDateTime now,
                                           Pageable pageable);

    // 3순위: 가장 최신 응모 시작한 드로우 (2시간마다 갱신)
    // 구현은 “ONGOING startAt desc”로 1개 뽑고, 캐시는 스케줄러에서 하면 됨.
    @Query("""
        select d.id
        from Draw d
        where (d.status = :active or d.status = :drawing)
          and d.startAt <= :now
          and d.endAt > :now
        order by d.startAt desc, d.createdAt desc
    """)
    Page<Long> findHot3RecentStartedIds(@Param("active") DrawStatus active,
                                        @Param("drawing") DrawStatus drawing,
                                        @Param("now") LocalDateTime now,
                                        Pageable pageable);
}
