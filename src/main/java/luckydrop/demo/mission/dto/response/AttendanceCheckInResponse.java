package luckydrop.demo.mission.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceCheckInResponse {

    private boolean success;

    private Long userId;
    private String periodKey;      // yyyyMMdd
    private int streak;            // 오늘 처리 후 streak

    private int earnedTickets;     // 기본 출첵 보상
    private int bonusTickets;      // 7/30 보너스 합산
    private int totalEarnedTickets; // earned + bonus

    private String message;
}
