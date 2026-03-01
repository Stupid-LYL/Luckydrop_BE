package luckydrop.demo.draw.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.bookmark.service.DrawBookmarkService;
import luckydrop.demo.draw.dto.response.DrawDetailResponse;
import luckydrop.demo.draw.dto.response.DrawSummaryResponse;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawStatus;
import luckydrop.demo.draw.repository.DrawRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

//Draw 조회용
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrawQueryService {

    private final DrawBookmarkService drawBookmarkService;
    private final DrawRepository drawRepository;

    //userID 받아서 isbookmark 붙이기
    public Page<DrawSummaryResponse> getDraws(Long userId, Pageable pageable) {

        Page<Draw> page = drawRepository.findAllByStatusNot(DrawStatus.CANCEL, pageable);

        List<Long> drawIds = page.getContent().stream()
                .map(Draw::getId)
                .toList();

        Set<Long> bookmarkedIds = drawBookmarkService.findBookmarkedDrawIds(userId, drawIds);

        // ✅ drawId별 북마크 카운트 한 방에
        var bookmarkCountMap = drawBookmarkService.findBookmarkCountMap(drawIds);

        return page.map(draw -> {
            Long drawId = draw.getId();
            boolean isBookmarked = bookmarkedIds.contains(drawId);
            long bookmarkCount = bookmarkCountMap.getOrDefault(drawId, 0L);

            return DrawSummaryResponse.from(draw, isBookmarked, bookmarkCount);
        });
    }

    // 단건 exist로 체크
    public DrawDetailResponse getDraw(Long userId, Long drawId) {
        Draw draw = drawRepository.findByIdAndStatusNot(drawId, DrawStatus.CANCEL)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 드로우입니다."));

        boolean isBookmarked = drawBookmarkService.isBookmarked(userId, drawId);
        long bookmarkCount = drawBookmarkService.getBookmarkCount(drawId);

        return DrawDetailResponse.from(draw, isBookmarked, bookmarkCount);
    }
}
