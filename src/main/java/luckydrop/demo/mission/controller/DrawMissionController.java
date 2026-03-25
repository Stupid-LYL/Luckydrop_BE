package luckydrop.demo.mission.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.mission.service.DrawMissionClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions/draw")
public class DrawMissionController {

    private final DrawMissionClaimService drawMissionClaimService;

    @PostMapping("/first/claim")
    public ResponseEntity<Void> claimFirstDrawMission(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUser().getId();
        drawMissionClaimService.claimFirstDrawMission(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/daily/claim")
    public ResponseEntity<Void> claimDailyDrawMission(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUser().getId();
        drawMissionClaimService.claimDailyDrawMission(userId);
        return ResponseEntity.ok().build();
    }
}