package luckydrop.demo.entry.service;

import luckydrop.demo.ticket.dto.request.TicketUseReqDto;
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

    /**
     * @param drawId
     * @param userId
     * @param count 응모 장수(>=1)
     */

    @Transactional
    public DrawEntryResponse enter(Long drawId, Long userId, int count, String idempotencyKey) {

        // draw 락 조회 (상태/시간/취소와 레이스 방지)
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("draw not found: " + drawId));

        // 정책 검증
        validateEnterable(draw, userId, LocalDateTime.now());

        // 필요 티켓 계산
        long totalCostLong = Math.multiplyExact((long) draw.getTicketCostEntry(), (long) count);

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
                        .refId(String.valueOf(drawId))
                        .idempotencyKey(idempotencyKey)
                        .build());

        //응모 누적 upsert
        entrySummaryRepository.upsertIncrease(drawId, userId, count);

        long totalCount = getCurrentEntryCount(drawId, userId);

        return DrawEntryResponse.builder()
                .drawId(drawId)
                .userId(userId)
                .addedCount(count)
                .totalCount(totalCount)
                .build();
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
        if (!"ACTIVE".equals(String.valueOf(draw.getStatus()))) {
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
}
