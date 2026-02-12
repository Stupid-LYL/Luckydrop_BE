package luckydrop.demo.draw.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.dto.response.DrawDetailResponse;
import luckydrop.demo.draw.dto.response.DrawSummaryResponse;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawStatus;
import luckydrop.demo.draw.repository.DrawRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Draw 조회용
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DrawQueryService {

    private final DrawRepository drawRepository;

    public Page<DrawSummaryResponse> getDraws(Pageable pageable) {
        return drawRepository.findAllByStatusNot(DrawStatus.CANCEL, pageable)
                .map(DrawSummaryResponse::from);
    }

    public DrawDetailResponse getDraw(Long drawId) {
        Draw draw = drawRepository.findByIdAndStatusNot(drawId, DrawStatus.CANCEL)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 드로우입니다."));

        return DrawDetailResponse.from(draw);
    }
}
