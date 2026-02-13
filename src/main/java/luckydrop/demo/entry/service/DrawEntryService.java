package luckydrop.demo.entry.service;

import jakarta.transaction.Transactional;
import luckydrop.demo.draw.entity.Draw;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.repository.DrawEntrySummaryRepository;
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
    public DrawEntryResponse enter(Long drawId, Long userId, int count) {

        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("draw not found: " + drawId));

        validateEnterable(draw, LocalDateTime.now());

        //오버플로우 방지
        long totalCost = Math.multiplyExact((long) draw.getTicketCostEntry(), (long) count);

        //티켓 차감 + 원장 기록 (TicketService 아래에 추가할 메서드)
        /*
        ticketService.useTickets(
                userId,
                totalCost,
                "DRAW_ENTRY",
                drawId,
                "드로우 응모 " + count + "회" //reason
        ); */

        entrySummaryRepository.upsertIncrease(drawId, userId, count);

        Long totalCount = entrySummaryRepository.findEntryCount(drawId, userId);
        long safeTotal = (totalCount == null) ? 0L : totalCount;

        return DrawEntryResponse.builder()
                .drawId(drawId)
                .userId(userId)
                .addedCount(count)
                .totalCount(safeTotal)
                .build();
    }

    private void validateEnterable(Draw draw, LocalDateTime now) {

        if (!"ACTIVE".equals(String.valueOf(draw.getStatus()))) {
            throw new IllegalStateException("draw not active. status= " + draw.getStatus());
        }

        if (now.isBefore(draw.getStartAt())) {
            throw new IllegalStateException("draw not started yet");
        }

        if (now.isAfter(draw.getEndAt())) {
            throw new IllegalStateException("draw already ended");
        }
    }
}
