package luckydrop.demo.mission.controller;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.mission.dto.response.AttendanceCheckInResponse;
import luckydrop.demo.mission.service.AttendanceService;
//import org.springframework.context.annotation.Profile; // 테스트
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/missions/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
//    @Profile({"local", "dev"}) // 테스트 끝나면 지우기
    public ResponseEntity<AttendanceCheckInResponse> checkIn(
            @AuthenticationPrincipal CustomUserPrincipal principal
//            @RequestParam(value = "overrideDate", required = false) String overrideDate // 테스트
    ) {
        Long userId = principal.getUser().getId();
        return ResponseEntity.ok(attendanceService.checkIn(userId));
    }
}
