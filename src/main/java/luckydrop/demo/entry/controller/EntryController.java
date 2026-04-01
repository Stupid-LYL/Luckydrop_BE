package luckydrop.demo.entry.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.entry.dto.request.DrawEntryRequest;
import luckydrop.demo.entry.dto.request.MyEntryListRequest;
import luckydrop.demo.entry.dto.response.DrawEntryResponse;
import luckydrop.demo.entry.dto.response.MyEntryResponse;
import luckydrop.demo.entry.dto.response.MyEntryStatsResponse;
import luckydrop.demo.entry.service.DrawEntryService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/draws")
@Tag(name = "Draw Entry", description = "드로우 응모 및 내 응모내역 조회 API")
public class EntryController {

    private final DrawEntryService drawEntryService;


    @Operation(
            summary = "드로우 응모",
            description = "로그인한 사용자가 특정 드로우에 응모한다. "
                    + "중복 요청 방지를 위해 Idempotency-Key 헤더가 필수이다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "응모 성공",
                    content = @Content(schema = @Schema(implementation = DrawEntryResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 응모 불가 상태"),
            @ApiResponse(responseCode = "401", description = "로그인 필요"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 드로우")
    })
    @PostMapping("/{drawId}/entry")
    public ResponseEntity<DrawEntryResponse> enter(
            @Parameter(description = "응모할 드로우 ID", example = "1")
            @PathVariable Long drawId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "응모 요청 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DrawEntryRequest.class))
            )
            @Valid @RequestBody DrawEntryRequest req,

            @Parameter(description = "멱등성 보장을 위한 요청 키", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader(value = "Idempotency-Key", required = true) String idempotencyKey,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
            ) {
        DrawEntryResponse res = drawEntryService.enter(
                drawId,
                principal.getUser().getId(),
                req.getCount(),
                idempotencyKey
        );
        return ResponseEntity.ok(res);
    }


    @Operation(
            summary = "내 응모 내역 조회",
            description = "로그인한 사용자의 응모 내역을 조건에 따라 페이징 조회한다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MyEntryResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    @GetMapping("/my-entries")
    public ResponseEntity<Page<MyEntryResponse>> getMyEntries(
            @Parameter(hidden = true)
            MyEntryListRequest req,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Page<MyEntryResponse> entries = drawEntryService.getMyEntries(principal.getUser().getId(), req);
        return ResponseEntity.ok(entries);
    }


    @Operation(
            summary = "내 응모 통계 조회",
            description = "로그인한 사용자의 전체 응모 수, 당첨 수 등 응모 통계를 조회한다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MyEntryStatsResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "로그인 필요")
    })
    @GetMapping("/my-entries/stats")
    public ResponseEntity<MyEntryStatsResponse> getMyEntryStats(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        MyEntryStatsResponse stats = drawEntryService.getMyEntryStats(principal.getUser().getId());
        return ResponseEntity.ok(stats);
    }
}
