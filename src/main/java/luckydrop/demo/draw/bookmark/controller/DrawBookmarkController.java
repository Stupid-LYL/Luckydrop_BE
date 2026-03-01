package luckydrop.demo.draw.bookmark.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.draw.bookmark.dto.response.BookmarkedDrawSummaryResponse;
import luckydrop.demo.draw.bookmark.dto.response.MyBookmarkListResponse;
import luckydrop.demo.draw.bookmark.service.DrawBookmarkQueryService;
import luckydrop.demo.draw.bookmark.service.DrawBookmarkService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/draws")
public class DrawBookmarkController {

    private final DrawBookmarkService drawBookmarkService;
    private final DrawBookmarkQueryService drawBookmarkQueryService;

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

    //추후 마이페이지 쪽 컨트롤러로 옮겨야할 듯
    @GetMapping("/bookmarks")
    public ResponseEntity<MyBookmarkListResponse<BookmarkedDrawSummaryResponse>> myBookmark(
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @PageableDefault(size = 20) Pageable pageable
            ) {
        return ResponseEntity.ok(
                drawBookmarkQueryService.getMyBookmark(principal.getUser().getId(), pageable)
        );
    }
}