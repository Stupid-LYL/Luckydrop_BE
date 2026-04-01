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
import luckydrop.demo.draw.dto.request.AdminDrawForceCancelRequest;
import luckydrop.demo.draw.dto.response.AdminDrawSummaryResponse;
import luckydrop.demo.draw.service.DrawCancelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Draw Cancel", description = "드로우 취소 및 관리자 강제 취소 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class DrawCancelController {

    private final DrawCancelService drawCancelService;


    @Operation(
            summary = "호스트 드로우 취소",
            description = "드로우 생성자가 자신의 드로우를 취소한다." + "일반적으로 취소 가능한 상태에서만 수행 가능하며," +
                    "성공 시 204 No Content를 반환한다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "드로우 취소 성공"),
            @ApiResponse(responseCode = "400", description = "취소할 수 없는 상태의 드로우"),
            @ApiResponse(responseCode = "403", description = "해당 드로우를 취소할 권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 드로우")
    })
    @DeleteMapping("/draws/{id}")
    public ResponseEntity<Void> deleteDraw(
            @Parameter(description = "취소할 드로우 ID", example = "1")
            @PathVariable("id") Long id,
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long requesterUserId = principal.getUser().getId();

        drawCancelService.cancelByHost(id, requesterUserId);
        return ResponseEntity.noContent().build(); //204
    }


    @Operation(
            summary = "관리자 강제 취소",
            description = "관리자가 특정 드로우를 강제 취소한다. " +
                    "취소 사유 코드와 상세 사유를 함께 저장할 수 있다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "강제 취소 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 취소 불가능한 상태"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 드로우")
    })
    @PostMapping("/admin/draws/{id}/force-cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> forceCancel(
            @PathVariable("id") Long drawId,
            @Valid @RequestBody AdminDrawForceCancelRequest request
            ) {

        drawCancelService.cancelByAdmin(drawId, request.reasonCode(), request.reasonDetail());

        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "강제 취소된 드로우 목록 조회",
            description = "관리자가 강제 취소된 드로우 목록을 조회한다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = AdminDrawSummaryResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @GetMapping("/admin/draws")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminDrawSummaryResponse>> getForceCancelDraws() {
        return ResponseEntity.ok(drawCancelService.getForceCancelDraws());
    }
}
