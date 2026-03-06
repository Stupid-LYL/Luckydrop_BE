package luckydrop.demo.draw.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.draw.service.DrawCancelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@Service
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> forceCancel(
            @PathVariable("id") Long drawId,
            @RequestParam(required = false, defaultValue = "HOST_REQUEST") String reasonCode,
            @RequestParam(required = false, defaultValue = "") String reasonDetail
    ) {

        drawCancelService.cancelByAdmin(drawId, reasonCode, reasonDetail);

        return ResponseEntity.noContent().build();
    }
}
