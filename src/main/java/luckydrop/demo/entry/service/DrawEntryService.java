package luckydrop.demo.entry.service;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("🎯 enterDraw: user={}, draw={}, count={}, tickets={}",
                userId, drawId, count, idempotencyKey);

        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("draw not found: " + drawId));

        validateEnterable(draw, userId, LocalDateTime.now());

        int ticketPerEntry = draw.getTicketCostEntry();
        long totalCostLong = Math.multiplyExact((long) ticketPerEntry, (long) count);
        if (totalCostLong > Integer.MAX_VALUE) {
            throw new IllegalStateException("ticket cost too large");
        }
        int totalCost = (int) totalCostLong;

        // ✅ 1. 티켓 차감
        ticketService.useTickets(TicketUseReqDto.builder()
                .userId(userId)
                .amount(totalCost)
                .reason("DRAW_ENTRY")
                .refType("DRAW")
                .refId(drawId)
                .idempotencyKey(idempotencyKey)
                .build());

        // ✅ 2. 응모 횟수 증가 (count, NOT totalCost!)
        upsertEntrySummary(drawId, userId, count);

        long spentTicketsTotal = getCurrentEntryCount(drawId, userId);

        log.info("✅ 응모 완료: count={}, tickets={}, total={}", count, totalCost, spentTicketsTotal);

        return DrawEntryResponse.builder()
                .drawId(drawId)
                .userId(userId)
                .spentTicketsAdded(totalCost)
                .spentTicketsTotal(spentTicketsTotal)
                .entryTimesAdded(count)
                .build();
    }

    private DrawEntrySummary upsertEntrySummary(Long drawId, Long userId, int entryCountToAdd) {
        DrawEntrySummaryId id = new DrawEntrySummaryId(drawId, userId);

        DrawEntrySummary summary = entrySummaryRepository.findById(id)
                .map(existing -> {
                    log.info("📊 기존: {} → +{}", existing.getEntryCount(), entryCountToAdd);
                    existing.setEntryCount(existing.getEntryCount() + entryCountToAdd);
                    return existing;
                })
                .orElseGet(() -> {
                    log.info("➕ 신규: draw={}, user={}, count={}", drawId, userId, entryCountToAdd);
                    DrawEntrySummary newOne = new DrawEntrySummary();
                    newOne.setDrawId(drawId);
                    newOne.setUserId(userId);
                    newOne.setEntryCount((long) entryCountToAdd);
                    return newOne;
                });

        return entrySummaryRepository.save(summary);
    }

    private long getCurrentEntryCount(Long drawId, Long userId) {
        Long totalCount = entrySummaryRepository.findEntryCount(drawId, userId);
        return totalCount == null ? 0L : totalCount;
    }

    private void validateEnterable(Draw draw, Long userId, LocalDateTime now) {
        if (draw.getUserId().equals(userId)) {
            throw new IllegalStateException("host cannot enter own draw");
        }
        if (draw.getStatus() != DrawStatus.ACTIVE) {
            throw new IllegalStateException("draw not active. status=" + draw.getStatus());
        }
        if (now.isBefore(draw.getStartAt())) {
            throw new IllegalStateException("draw not started yet");
        }
        if (!now.isBefore(draw.getEndAt())) {
            throw new IllegalStateException("draw already ended");
        }
    }

    public Page<MyEntryResponse> getMyEntries(Long userId, MyEntryListRequest req) {
        log.info("🔍 getMyEntries - userId: {}, search: {}, status: {}, page: {}/{}",
                userId, req.getSearch(), req.getStatus(), req.getPage(), req.getSize());

        Page<MyEntryResponse> entries = entrySummaryRepository.findMyEntries(
                userId, req.getSearch(), req.getStatus(), req.getFromDate(),
                req.getToDate(), PageRequest.of(req.getPage(), req.getSize()));

        log.info("📊 결과: total={}, size={}", entries.getTotalElements(), entries.getContent().size());
        return entries;
    }

    public MyEntryStatsResponse getMyEntryStats(Long userId) {
        log.info("📈 getMyEntryStats - userId: {}", userId);

        Long total = entrySummaryRepository.countTotalByUserId(userId);
        Long inProgress = entrySummaryRepository.countInProgressByUserId(userId);
        Long won = entrySummaryRepository.countWonByUserId(userId);
        Long tickets = entrySummaryRepository.sumTicketUsedByUserId(userId);

        log.info("📊 RAW: total={}, inProgress={}, won={}, tickets={}", total, inProgress, won, tickets);

        MyEntryStatsResponse stats = new MyEntryStatsResponse();
        stats.setTotal(total);
        stats.setInProgress(inProgress);
        stats.setWon(won);
        stats.setTotalTicketsUsed(tickets);
        return stats;
    }
}
