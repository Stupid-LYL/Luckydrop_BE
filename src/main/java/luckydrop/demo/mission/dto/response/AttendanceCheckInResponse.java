package luckydrop.demo.mission.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceCheckInResponse {

    private boolean success;
    private boolean alreadyCheckedIn; // 추가: 오늘 이미 출첵이면 true

    private Long userId;
    private String periodKey;      // yyyyMMdd
    private int streak;            // 오늘 처리 후 streak (30보너스면 0이 될 수 있음)

    private int earnedTickets;      // 기본 출첵 보상(이번 요청에서 지급된 양)
    private int bonusTickets;       // 이번 요청에서 지급된 보너스 합산
    private int totalEarnedTickets; // earned + bonus

    private String message;
}
