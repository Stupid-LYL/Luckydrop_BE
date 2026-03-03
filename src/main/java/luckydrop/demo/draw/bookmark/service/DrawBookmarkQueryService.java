package luckydrop.demo.draw.bookmark.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.bookmark.dto.response.BookmarkedDrawSummaryResponse;
import luckydrop.demo.draw.bookmark.dto.response.MyBookmarkListResponse;
import luckydrop.demo.draw.bookmark.repository.DrawBookmarkRepository;
import luckydrop.demo.draw.bookmark.repository.BookmarkedDrawSummaryView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrawBookmarkQueryService {

    private final DrawBookmarkRepository drawBookmarkRepository;

    public MyBookmarkListResponse<BookmarkedDrawSummaryResponse> getMyBookmark(Long userId, Pageable pageable) {

        Page<BookmarkedDrawSummaryView> page = drawBookmarkRepository.findMyBookmarkedDraws(userId, pageable);

        List<BookmarkedDrawSummaryResponse> items = page.getContent().stream()
                .map(v -> BookmarkedDrawSummaryResponse.builder()
                        .drawId(v.getDrawId())
                        .title(v.getTitle())
                        .status(v.getStatus())
                        .startAt(toLdt(v.getStartAt()))
                        .endAt(toLdt(v.getEndAt()))
                        .bookmarkCount(nvl(v.getBookmarkCount()))
                        .participants(nvl(v.getParticipants()))
                        .thumbnailUrl(v.getThumbnailUrl())
                        .isBookmarked(true)
                        .bookmarkedAt(toLdt(v.getBookmarkedAt()))
                        .build())
                .toList();

        return MyBookmarkListResponse.<BookmarkedDrawSummaryResponse>builder()
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .items(items)
                .build();
    }

    private long nvl(Long v) { return v == null ? 0L : v; }

    private LocalDateTime toLdt(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }
}