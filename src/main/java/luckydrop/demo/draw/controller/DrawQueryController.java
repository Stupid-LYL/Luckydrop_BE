package luckydrop.demo.draw.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.dto.response.DrawDetailResponse;
import luckydrop.demo.draw.dto.response.DrawSummaryResponse;
import luckydrop.demo.draw.service.DrawQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/draws")
public class DrawQueryController {

    private final DrawQueryService drawQueryService;

    @GetMapping
    public Page<DrawSummaryResponse> getDraws(Pageable pageable) {
        return drawQueryService.getDraws(pageable);
    }

    @GetMapping("/{drawId}")
    public DrawDetailResponse getDraw(@PathVariable Long drawId) {
        return drawQueryService.getDraw(drawId);
    }
}
