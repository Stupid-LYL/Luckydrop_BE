package luckydrop.demo.mission.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.mission.service.AdMissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions/ad")
public class AdMissionController {

    private final AdMissionService adMissionService;

    @PostMapping("/complete")
    public ResponseEntity<Void> completeAdMission(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {

        Long userId = principal.getUser().getId();

        adMissionService.completeAdMission(userId);

        return ResponseEntity.ok().build();
    }
}