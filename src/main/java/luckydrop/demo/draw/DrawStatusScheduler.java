package luckydrop.demo.draw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.draw.repository.DrawRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DrawStatusScheduler {

    private final DrawRepository drawRepository;

    @Scheduled(fixedDelay = 30_000)
    @Transactional
    public void updateDrawStatus()
    {
        LocalDateTime now = LocalDateTime.now();

        int activated = drawRepository.updateDraftToActive(now);
        int drawing = drawRepository.updateActiveToDrawing(now);

        if (activated > 0 || drawing > 0) {
            log.info("[DrawScheduler] DRAFT -> ACTIVE: {}, ACTIVE -> DRAWING: {}", activated, drawing);
        }

        log.info("[DrawScheduler tick] now={}", now);
        log.info("[DrawScheduler result] DRAFT->ACTIVE={}, ACTIVE->DRAWING={}", activated, drawing);

    }
}
