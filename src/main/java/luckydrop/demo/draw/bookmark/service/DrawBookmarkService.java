package luckydrop.demo.draw.bookmark.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.bookmark.entity.DrawBookmark;
import luckydrop.demo.draw.bookmark.repository.DrawBookmarkRepository;
import luckydrop.demo.draw.repository.DrawRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DrawBookmarkService {

    private final DrawBookmarkRepository drawBookmarkRepository;
    private final DrawRepository drawRepository;

    //찜하기
    public void bookmark(Long userId, Long drawId) {
        if (!drawRepository.existsById(drawId)) {
            throw new IllegalArgumentException("존재하지 않는 드로우입니다.");
        }

        //멱등성
        if (drawBookmarkRepository.existsByUserIdAndDrawId(userId, drawId)) {
            return;
        }

        drawBookmarkRepository.save(DrawBookmark.of(userId, drawId));
    }

    //찜 취소
    public void unBookmark(Long userId, Long drawId) {
        drawBookmarkRepository.deleteByUserIdAndDrawId(userId, drawId);
    }

    //상세 조회시 단건 체크
    public boolean isBookmarked(Long userId, Long drawId) {
        return drawBookmarkRepository.existsByUserIdAndDrawId(userId, drawId);
    }

    //드로우 목록 조회 N + 1 방지용
    public Set<Long> findBookmarkedDrawIds(Long userId, List<Long> drawIds) {

        if (drawIds.isEmpty()) {
            return Set.of();
        }

        List<Long> bookmarkedIds =
                drawBookmarkRepository.findBookmarkedDrawIds(userId, drawIds);

        return new HashSet<>(bookmarkedIds);
    }

    //내 북마크 조회
    public List<DrawBookmark> findMyBookmarks(Long userId, Pageable pageable) {
        return drawBookmarkRepository.findMyBookmarks(userId, pageable);
    }

    //북마크 ID 뿌리는용
    public List<Long> findMyBookmarkedDrawIds(Long userId, Pageable pageable) {
        return drawBookmarkRepository.findMyBookmarkedDrawIds(userId, pageable);
    }
}
