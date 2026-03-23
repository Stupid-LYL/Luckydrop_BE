package luckydrop.demo.draw.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.draw.dto.request.AdminDrawForceCancelRequest;
import luckydrop.demo.draw.service.DrawCancelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class DrawCancelController {

    private final DrawCancelService drawCancelService;

    @DeleteMapping("/draws/{id}")
    public ResponseEntity<Void> deleteDraw(@PathVariable("id") Long id,
                                           @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long requesterUserId = principal.getUser().getId();

        drawCancelService.cancelByHost(id, requesterUserId);
        return ResponseEntity.noContent().build(); //204
    }

    @PostMapping("/admin/draws/{id}/force-cancel")
    @PreAuthorize("hasRole('ADMIN')") //추후 ADMIN ROLE 생길 경우에 사용
    public ResponseEntity<Void> forceCancel(
            @PathVariable("id") Long drawId,
            @Valid @RequestBody AdminDrawForceCancelRequest request
            ) {

        drawCancelService.cancelByAdmin(drawId, request.reasonCode(), request.reasonDetail());

        return ResponseEntity.noContent().build();
    }
}
