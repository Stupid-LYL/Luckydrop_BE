package luckydrop.demo.entry.service;

import luckydrop.demo.draw.entity.DrawEntrySummary;
import luckydrop.demo.draw.entity.DrawEntrySummaryId;
import luckydrop.demo.draw.enums.DrawStatus;
import luckydrop.demo.entry.dto.request.MyEntryListRequest;
import luckydrop.demo.entry.dto.response.MyEntryResponse;
import luckydrop.demo.entry.dto.response.MyEntryStatsResponse;
import luckydrop.demo.ticket.dto.request.TicketUseReqDto;
import luckydrop.demo.ticket.enums.TicketHistoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import luckydrop.demo.draw.entity.Draw;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.entry.repository.DrawEntrySummaryRepository;
import luckydrop.demo.draw.repository.DrawRepository;
import luckydrop.demo.entry.dto.response.DrawEntryResponse;
import luckydrop.demo.ticket.service.TicketService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DrawEntryService {

    private final DrawRepository drawRepository;
    private final DrawEntrySummaryRepository entrySummaryRepository;
    private final TicketService ticketService;

    //정책값: 1회 최대 응모수
    //private static final int MAX_ENTRY_PER_REQUEST = 1000;

    @Transactional
    public DrawEntryResponse enter(Long drawId, Long userId, int count, String idempotencyKey) {

        // draw 락 조회 (상태/시간/취소와 레이스 방지)
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("draw not found: " + drawId));

        // 정책 검증
        validateEnterable(draw, userId, LocalDateTime.now());

        // 1회 응모당 티켓 비용
        int ticketPerEntry = draw.getTicketCostEntry();

        // 이번 요청에서 총 사용 티켓(= 가중치)
        long totalCostLong = Math.multiplyExact((long) ticketPerEntry, (long) count);
        if (totalCostLong > Integer.MAX_VALUE) {
            throw new IllegalStateException("ticket cost too large");
        }
        int totalCost = (int) totalCostLong;

        //티켓 차감 + 원장 기록 (TicketService 아래에 추가할 메서드)
        ticketService.useTickets(
                TicketUseReqDto.builder()
                        .userId(userId)
                        .amount(totalCost)
                        .reason("DRAW_ENTRY")
                        .refType("DRAW")
                        .refId(drawId)
                        .idempotencyKey(idempotencyKey)
                        .build());

        // 총 사용한 티켓
        upsertEntrySummary(drawId, userId, totalCost);

        // 누적 사용 티켓
        long spentTicketsTotal = getCurrentEntryCount(drawId, userId);

        return DrawEntryResponse.builder()
                .drawId(drawId)
                .userId(userId)
                .spentTicketsAdded(totalCost) // 이번에 쓴 티켓
                .spentTicketsTotal(spentTicketsTotal) // 누적 티켓
                .entryTimesAdded(count) // 이번 응모 횟수
                .build();
    }

    /**
     * 엔티티 기반 업서트:
     * - 이미 있으면 entryCount만 증가
     * - 없으면 새로 생성
     * - Auditing (createdAt, updatedAt) 자동 적용
     */
    private DrawEntrySummary upsertEntrySummary(Long drawId, Long userId, int entryCountToAdd) {
        DrawEntrySummaryId id = new DrawEntrySummaryId(drawId, userId);

        DrawEntrySummary summary = entrySummaryRepository.findById(id)
                .map(existing -> {
                    // 존재하는 레코드: entry_count 증가
                    existing.setEntryCount(existing.getEntryCount() + entryCountToAdd);
                    return existing;
                })
                .orElseGet(() -> {
                    // 신규 레코드: 생성
                    DrawEntrySummary newOne = new DrawEntrySummary();
                    newOne.setDrawId(drawId);
                    newOne.setUserId(userId);
                    newOne.setEntryCount((long) entryCountToAdd);
                    return newOne;
                });

        // Auditing: createdAt / updatedAt 자동 적용
        return entrySummaryRepository.save(summary);
    }

    private long getCurrentEntryCount(Long drawId, Long userId) {
        Long totalCount = entrySummaryRepository.findEntryCount(drawId, userId);
        return totalCount == null ? 0L : totalCount;
    }

    private void validateEnterable(Draw draw,Long userId ,LocalDateTime now) {

        // 본인 드로우 금지
        if (draw.getUserId().equals(userId)) {
            throw new IllegalStateException("host cannot enter own draw");
        }

        // ACTIVE 상태만 가능
        if (draw.getStatus() != DrawStatus.ACTIVE) {
            throw new IllegalStateException("draw not active. status= " + draw.getStatus());
        }

        //응모 시간 비교
        if (now.isBefore(draw.getStartAt())) {
            throw new IllegalStateException("draw not started yet");
        }
        //endAt 이후는 불가
        if (!now.isBefore(draw.getEndAt())) { // now >= endAt
            throw new IllegalStateException("draw already ended");
        }
    }

    public Page<MyEntryResponse> getMyEntries(Long userId, MyEntryListRequest req) {
        return entrySummaryRepository.findMyEntries(
                userId,
                req.getSearch(),
                req.getStatus(),
                req.getFromDate(),
                req.getToDate(),
                PageRequest.of(req.getPage(), req.getSize())
        );
    }

    public MyEntryStatsResponse getMyEntryStats(Long userId) {
        MyEntryStatsResponse stats = new MyEntryStatsResponse();
        stats.setTotal((long) entrySummaryRepository.countTotalByUserId(userId).intValue());
        stats.setInProgress((long) entrySummaryRepository.countInProgressByUserId(userId).intValue());
        stats.setWon((long) entrySummaryRepository.countWonByUserId(userId).intValue());
        stats.setTotalTicketsUsed((long) entrySummaryRepository.sumTicketUsedByUserId(userId, TicketHistoryType.USE).intValue());
        return stats;
    }
}
