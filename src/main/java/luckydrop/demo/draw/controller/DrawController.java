package luckydrop.demo.draw.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.draw.dto.request.DrawCreateRequest;
import luckydrop.demo.draw.dto.request.DrawUpdateRequest;
import luckydrop.demo.draw.dto.response.*;
import luckydrop.demo.draw.service.DrawService;
import luckydrop.demo.draw.service.DrawingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/draws")
@Tag(name = "Draw", description = "드로우 생성, 수정, 추첨 API")
public class DrawController {

    private final DrawService drawService;
    private final DrawingService drawingService;

    @Operation(
            summary = "드로우 생성",
            description = "HOST Role을 부여받은 사용자는 새로운 드로우를 생성한다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "드로우 생성 성공",
                    content = @Content(schema = @Schema(implementation = DrawCreateResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청값 검증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            )
    })
    @PostMapping("/create")
    public ResponseEntity<DrawCreateResponse> createDraw(@RequestBody @Valid DrawCreateRequest request,
                                                         @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUser().getId();
        Long drawId = drawService.createDraw(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DrawCreateResponse.of(drawId));
    }


    @Operation(
            summary = "드로우 수정",
            description = "드로우 생성자가 기존 드로우 정보를 수정한다."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "드로우 수정 성공",
                    content = @Content(schema = @Schema(implementation = DrawDetailResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청값 검증 실패 또는 수정 불가능한 상태",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증되지 않은 사용자"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "수정 권한 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "드로우를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/{drawId}")
    public DrawDetailResponse updateDraw(
            @Parameter(description = "수정할 드로우 ID", example = "10")
            @PathVariable Long drawId,
            @AuthenticationPrincipal CustomUserPrincipal principal,
            @RequestBody @Valid DrawUpdateRequest request
            ) {
        Long requesterUserId = principal.getUser().getId();

        return drawService.updateDraw(drawId, requesterUserId, request);
    }

    @Operation(
            summary = "드로우 추첨 실행",
            description = "스케줄러 사용 자동 추첨 API"
    )
    @PostMapping("/{drawId}/draw")
    public ResponseEntity<Void> draw(@PathVariable Long drawId) {
        drawingService.drawingWinner(drawId);
        return ResponseEntity.ok().build();
    }

}
