package luckydrop.demo.draw.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.draw.dto.response.*;
import luckydrop.demo.draw.enums.DrawSort;
import luckydrop.demo.draw.enums.DrawTab;
import luckydrop.demo.draw.service.DrawQueryService;
import luckydrop.demo.draw.service.DrawingService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DrawQueryController {

    private final DrawQueryService drawQueryService;
    private final DrawingService drawingService;

    @GetMapping("/draws")
    public ResponseEntity<Page<DrawCardResponse>> getDraws(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestParam(defaultValue = "ALL") DrawTab tab,
            @RequestParam(required = false) DrawSort sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long userId = (principal == null) ? null : principal.getUser().getId();
        return ResponseEntity.ok(drawQueryService.getDraws(userId, tab, sort, page, size));
    }

    @GetMapping("/draws/{id}")
    public ResponseEntity<DrawDetailResponse> getDrawDetail(
            @PathVariable("id") Long drawId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = (principal == null) ? null : principal.getUser().getId();
        return ResponseEntity.ok(drawQueryService.getDrawDetail(drawId, userId));
    }

    @GetMapping("/draws/hot-banner")
    public ResponseEntity<HotBannerResponse> getHotBanner(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = (principal == null) ? null : principal.getUser().getId();
        return ResponseEntity.ok(drawQueryService.getHotBanner(userId));
    }

    @GetMapping("/draws/{drawId}/winners")
    public ResponseEntity<DrawWinnerResponse> getWinner(@PathVariable Long drawId) {
        return ResponseEntity.ok(drawingService.getWinner(drawId));
    }

    @GetMapping("/host/draws/{drawId}/winners")
    public ResponseEntity<List<HostWinnerInfoResponse>> getHostWinnerInfo(
            @PathVariable Long drawId,
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        List<HostWinnerInfoResponse> response = drawQueryService.getHostWinnerInfo(drawId, principal.getUser().getId());

        return ResponseEntity.ok(response);
    }
}
