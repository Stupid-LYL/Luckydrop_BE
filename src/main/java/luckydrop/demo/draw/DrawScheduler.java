package luckydrop.demo.draw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawStatus;
import luckydrop.demo.draw.repository.DrawRepository;
import luckydrop.demo.draw.service.DrawService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
//자동 추첨 전용 스케줄러
public class DrawScheduler {

    private final DrawRepository drawRepository;
    private final DrawService drawService;

    @Scheduled(fixedDelay =  60_000)
    public void runAutoDraw() {

        LocalDateTime now = LocalDateTime.now();

        List<Draw> drawingDraws =
                drawRepository.findReadyForDrawing(DrawStatus.DRAWING, now);

        int succeeded = 0;
        int failed = 0;

        for (Draw d : drawingDraws) {
            try {
                drawService.drawWinner(d.getId()); //내부에서 DRAWING->CLOSED 선점 + winners 저장/빈리스트
                succeeded++;
            } catch (Exception e) {
                failed++;
                log.warn("[AutoDraw] drawId={} 실패 msg={}", d.getId(), e.getMessage());
            }
        }

        if (succeeded > 0 || failed > 0) {
            log.info("[AutoDraw] succeeded={}, failed={}", succeeded, failed);
        }
    }
}
