package luckydrop.demo.draw.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.bookmark.service.DrawBookmarkService;
import luckydrop.demo.draw.dto.response.DrawDetailResponse;
import luckydrop.demo.draw.dto.response.DrawCardResponse;
import luckydrop.demo.draw.dto.response.HotBannerResponse;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawSort;
import luckydrop.demo.draw.enums.DrawStatus;
import luckydrop.demo.draw.enums.DrawTab;
import luckydrop.demo.draw.repository.DrawQueryIdRepository;
import luckydrop.demo.draw.repository.DrawRepository;
import luckydrop.demo.entry.repository.DrawEntrySummaryRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrawQueryService {

    private final DrawBookmarkService drawBookmarkService;
    private final DrawRepository drawRepository;

    //  추가
    private final DrawQueryIdRepository drawQueryIdRepository;
    private final DrawEntrySummaryRepository drawEntrySummaryRepository;


    // 컨트롤러용 wrapper (page,size -> Pageable)
    public Page<DrawCardResponse> getDraws(Long userId, DrawTab tab, DrawSort sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getDraws(userId, tab, sort, pageable);
    }

    /**
     *  명세 기반 목록 조회 (탭/정렬/tie-break/participant/bookmark)
     */
    public Page<DrawCardResponse> getDraws(Long userId, DrawTab tab, DrawSort sortOrNull, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();

        DrawSort sort = resolveDefaultSort(tab, sortOrNull);
        if (tab == DrawTab.CLOSED) sort = DrawSort.ENDED_DESC; // CLOSED 고정

        Page<Long> idPage = findIdsByPolicy(tab, sort, now, pageable);
        List<Long> drawIds = idPage.getContent();

        if (drawIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, idPage.getTotalElements());
        }

        // draw 엔티티 배치 조회 + 순서 복원
        List<Draw> draws = drawRepository.findAllByIdIn(drawIds);
        Map<Long, Draw> drawMap = draws.stream().collect(Collectors.toMap(Draw::getId, Function.identity()));
        List<Draw> orderedDraws = drawIds.stream()
                .map(drawMap::get)
                .filter(Objects::nonNull)
                .toList();

        // isBookmarked + bookmarkCount (기존 서비스 그대로 사용)
        Set<Long> bookmarkedIds = drawBookmarkService.findBookmarkedDrawIds(userId, drawIds);
        var bookmarkCountMap = drawBookmarkService.findBookmarkCountMap(drawIds);

        // participantCount 배치
        Map<Long, Long> participantCountMap = drawEntrySummaryRepository.countParticipantsByDrawIds(drawIds).stream()
                .collect(Collectors.toMap(
                        DrawEntrySummaryRepository.DrawCountRow::getDrawId,
                        DrawEntrySummaryRepository.DrawCountRow::getCnt
                ));

        List<DrawCardResponse> content = orderedDraws.stream()
                .map(draw -> {
                    Long drawId = draw.getId();
                    boolean isBookmarked = bookmarkedIds.contains(drawId);
                    long bookmarkCount = bookmarkCountMap.getOrDefault(drawId, 0L);
                    long participantCount = participantCountMap.getOrDefault(drawId, 0L);
                    return DrawCardResponse.from(draw, isBookmarked, bookmarkCount, participantCount);
                })
                .toList();

        return new PageImpl<>(content, pageable, idPage.getTotalElements());
    }


    public DrawDetailResponse getDrawDetail(Long userId, Long drawId) {
        Draw draw = drawRepository.findByIdAndStatusNot(drawId, DrawStatus.CANCEL)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 드로우입니다."));

        boolean isBookmarked = drawBookmarkService.isBookmarked(userId, drawId);
        long bookmarkCount = drawBookmarkService.getBookmarkCount(drawId);

        long participantCount = drawEntrySummaryRepository.countParticipants(drawId);

        return DrawDetailResponse.from(draw, isBookmarked, bookmarkCount, participantCount);
    }

    /**
     * HOT 배너
     */
    public HotBannerResponse getHotBanner() {
        LocalDateTime now = LocalDateTime.now();

        // 1순위
        Page<Long> p1 = drawQueryIdRepository.findHot1PopularOngoingIds(
                DrawStatus.ACTIVE, DrawStatus.DRAWING, now, PageRequest.of(0, 1));
        if (!p1.isEmpty()) {
            return HotBannerResponse.builder().drawId(p1.getContent().get(0)).reason("POPULAR").build();
        }

        // 2순위
        Page<Long> p2 = drawQueryIdRepository.findHot2UpcomingBookmarkIds(
                DrawStatus.DRAFT, now, PageRequest.of(0, 1));
        if (!p2.isEmpty()) {
            return HotBannerResponse.builder().drawId(p2.getContent().get(0)).reason("UPCOMING_BOOKMARK").build();
        }

        // 3순위
        Page<Long> p3 = drawQueryIdRepository.findHot3RecentStartedIds(
                DrawStatus.ACTIVE, DrawStatus.DRAWING, now, PageRequest.of(0, 1));
        if (!p3.isEmpty()) {
            return HotBannerResponse.builder().drawId(p3.getContent().get(0)).reason("RECENT_STARTED").build();
        }

        return HotBannerResponse.builder().drawId(null).reason("EMPTY").build();
    }

    private DrawSort resolveDefaultSort(DrawTab tab, DrawSort sort) {
        if (sort != null) return sort;
        return DrawSort.LATEST;
    }

    private Page<Long> findIdsByPolicy(DrawTab tab, DrawSort sort, LocalDateTime now, Pageable pageable) {
        return switch (tab) {
            case ALL -> switch (sort) {
                //  전체 기본정렬: createdAt DESC
                case LATEST -> drawQueryIdRepository.findAllLatestIds(DrawStatus.CANCEL, pageable);
                default -> drawQueryIdRepository.findAllLatestIds(DrawStatus.CANCEL, pageable);
            };
            case UPCOMING -> switch (sort) {
                case LATEST -> drawQueryIdRepository.findAllLatestIds(DrawStatus.CANCEL, pageable);
                case BOOKMARK -> drawQueryIdRepository.findUpcomingBookmarkIds(DrawStatus.DRAFT, now, pageable);
                default -> drawQueryIdRepository.findUpcomingLatestIds(DrawStatus.DRAFT, now, pageable);
            };
            case ONGOING -> switch (sort) {
                case PARTICIPANT -> drawQueryIdRepository.findOngoingParticipantIds(DrawStatus.ACTIVE, DrawStatus.DRAWING, now, pageable);
                case BOOKMARK -> drawQueryIdRepository.findOngoingBookmarkIds(DrawStatus.ACTIVE, DrawStatus.DRAWING, now, pageable);
                case ENDING_SOON -> drawQueryIdRepository.findOngoingEndingSoonIds(DrawStatus.ACTIVE, DrawStatus.DRAWING, now, pageable);
                default -> drawQueryIdRepository.findOngoingStartedDescIds(DrawStatus.ACTIVE, DrawStatus.DRAWING, now, pageable);
            };
            case CLOSED -> drawQueryIdRepository.findClosedEndedDescIds(DrawStatus.CLOSE, now, pageable);
        };
    }
}