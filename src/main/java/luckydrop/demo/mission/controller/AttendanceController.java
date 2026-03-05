package luckydrop.demo.mission.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.mission.dto.response.AttendanceCheckInResponse;
import luckydrop.demo.mission.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    public ResponseEntity<AttendanceCheckInResponse> checkIn(
            @AuthenticationPrincipal CustomUserPrincipal principal
    ) {
        Long userId = principal.getUser().getId();
        return ResponseEntity.ok(attendanceService.checkIn(userId));
    }
}
