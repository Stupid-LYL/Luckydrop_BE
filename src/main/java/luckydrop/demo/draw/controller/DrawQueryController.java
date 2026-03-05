package luckydrop.demo.draw.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.draw.dto.response.DrawDetailResponse;
import luckydrop.demo.draw.dto.response.DrawSummaryResponse;
import luckydrop.demo.draw.dto.response.HotBannerResponse;
import luckydrop.demo.draw.enums.DrawSort;
import luckydrop.demo.draw.enums.DrawTab;
import luckydrop.demo.draw.service.DrawQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/draws")
public class DrawQueryController {

    private final DrawQueryService drawQueryService;

    @GetMapping
    public ResponseEntity<Page<DrawSummaryResponse>> getDraws(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "ALL") DrawTab tab,
            @RequestParam(required = false) DrawSort sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = (principal == null) ? null : principal.getUser().getId();
        return ResponseEntity.ok(drawQueryService.getDraws(userId, tab, sort, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrawDetailResponse> getDrawDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PathVariable Long id
    ) {
        Long userId = (principal == null) ? null : principal.getUser().getId();
        return ResponseEntity.ok(drawQueryService.getDrawDetail(id, userId));
    }

    @GetMapping("/hot")
    public ResponseEntity<HotBannerResponse> getHotBanner() {
        return ResponseEntity.ok(drawQueryService.getHotBanner());
    }
}
