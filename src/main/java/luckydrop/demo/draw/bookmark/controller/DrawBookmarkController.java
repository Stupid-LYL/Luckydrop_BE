package luckydrop.demo.draw.bookmark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

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

@Tag(name = "Draw Bookmark", description = "드로우 북마크 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/draws")
public class DrawBookmarkController {

    private final DrawBookmarkService drawBookmarkService;
    private final DrawBookmarkQueryService drawBookmarkQueryService;


    @Operation(
            summary = "드로우 북마크 추가",
            description = "로그인한 사용자가 특정 드로우를 북마크한다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "북마크 추가 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 드로우")
    })
    @PostMapping("/{drawId}/bookmark")
    public ResponseEntity<Void> bookmark(
            @Parameter(description = "북마크할 드로우 ID", example = "1")
            @PathVariable Long drawId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        drawBookmarkService.bookmark(
                principal.getUser().getId(),
                drawId
        );

        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "드로우 북마크 해제",
            description = "로그인한 사용자가 특정 드로우의 북마크를 해제한다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "북마크 해제 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 드로우 또는 북마크 정보 없음")
    })
    @DeleteMapping("/{drawId}/bookmark")
    public ResponseEntity<Void> unBookmark(
            @Parameter(description = "북마크 해제할 드로우 ID", example = "1")
            @PathVariable Long drawId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        drawBookmarkService.unBookmark(
                principal.getUser().getId(),
                drawId
        );
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "내 북마크 목록 조회",
            description = "로그인한 사용자의 북마크 드로우 목록을 페이징 조회한다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = MyBookmarkListResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
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