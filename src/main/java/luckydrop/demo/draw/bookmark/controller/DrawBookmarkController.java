package luckydrop.demo.draw.bookmark.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.draw.bookmark.service.DrawBookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/draws")
public class DrawBookmarkController {

    private final DrawBookmarkService drawBookmarkService;

    @PostMapping("/{drawId}/bookmark")
    public ResponseEntity<Void> bookmark(
            @PathVariable Long drawId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        drawBookmarkService.bookmark(
                principal.getUser().getId(),
                drawId
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{drawId}/bookmark")
    public ResponseEntity<Void> unBookmark(
            @PathVariable Long drawId,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        drawBookmarkService.unBookmark(
                principal.getUser().getId(),
                drawId
        );
        return ResponseEntity.noContent().build();
    }
}