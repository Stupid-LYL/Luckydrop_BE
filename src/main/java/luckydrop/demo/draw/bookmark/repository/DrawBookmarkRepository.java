package luckydrop.demo.draw.bookmark.repository;

import luckydrop.demo.draw.bookmark.entity.DrawBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface DrawBookmarkRepository extends JpaRepository<DrawBookmark, Long> {

    //단건 체크
    boolean existsByUserIdAndDrawId(Long userId, Long drawId);

    //찜 취소
    long deleteByUserIdAndDrawId(Long userId, Long drawId);

    //내가 북마크한 드로우 목록
    @Query("""
            select b.drawId
            from DrawBookmark b
            where b.userId = :userId
                and b.drawId in (:drawIds)
            """)
    List<Long> findBookmarkedDrawIds(@Param("userId") Long userId,
                                     @Param("drawIds") List<Long> drawIds);


    //최신순 페이징
    @Query("""
            select b
            from DrawBookmark b
            where b.userId = :userId
            order by b.createdAt desc
            """)
    List<DrawBookmark> findMyBookmarks(@Param("userId") Long userId, Pageable pageable);


    @Query("""
            select b.drawId
            from DrawBookmark b
            where b.userId = :userId
            order by b.createdAt desc
            """)
    List<Long> findMyBookmarkedDrawIds(@Param("userId") Long userId, Pageable pageable);
}
