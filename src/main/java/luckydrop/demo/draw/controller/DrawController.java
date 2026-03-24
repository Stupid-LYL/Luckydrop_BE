package luckydrop.demo.draw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.draw.dto.request.DrawCreateRequest;
import luckydrop.demo.draw.dto.request.DrawUpdateRequest;
import luckydrop.demo.draw.dto.response.*;
import luckydrop.demo.draw.service.DrawCancelService;
import luckydrop.demo.draw.service.DrawService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/draws")
public class DrawController {

    private final DrawService drawService;
    private final DrawCancelService drawCancelService;

    @PostMapping("/create")
    public ResponseEntity<DrawCreateResponse> createDraw(@RequestBody @Valid DrawCreateRequest request,
                                                         @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUser().getId();
        Long drawId = drawService.createDraw(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DrawCreateResponse.of(drawId));
    }

    @PatchMapping("/{drawId}")
    public DrawDetailResponse updateDraw(
            @PathVariable Long drawId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid DrawUpdateRequest request
            ) {
        Long requesterUserId = principal.getUser().getId();

        return drawService.updateDraw(drawId, requesterUserId, request);
    }

    @PostMapping("/{drawId}/draw")
    public ResponseEntity<Void> draw(@PathVariable Long drawId) {
        drawService.drawingWinner(drawId);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/{drawId}/winners")
    public ResponseEntity<DrawWinnerResponse> getWinner(@PathVariable Long drawId) {
        return ResponseEntity.ok(drawService.getWinner(drawId));
    }
}
