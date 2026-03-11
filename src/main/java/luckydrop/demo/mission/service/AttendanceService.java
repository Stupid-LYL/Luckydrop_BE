package luckydrop.demo.mission.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.mission.dto.response.AttendanceCheckInResponse;
import luckydrop.demo.mission.entity.Mission;
import luckydrop.demo.mission.entity.UserMission;
import luckydrop.demo.mission.repository.MissionRepository;
import luckydrop.demo.mission.repository.UserMissionRepository;
import luckydrop.demo.ticket.dto.request.TicketEarnReqDto;
import luckydrop.demo.ticket.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private static final DateTimeFormatter D = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    private static final String CODE_STATE = "ATTENDANCE_STATE";
    private static final String CODE_DAILY = "ATTENDANCE_DAILY";

    private static final Map<Integer, String> BONUS_CODES = Map.of(
            7, "ATTENDANCE_BONUS_7",
            14, "ATTENDANCE_BONUS_14",
            21, "ATTENDANCE_BONUS_21",
            30, "ATTENDANCE_BONUS_30"
    );

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final TicketService ticketService;

    @Transactional
    public AttendanceCheckInResponse checkIn(Long userId) {
        LocalDate today = LocalDate.now();
        String todayKey = today.format(D);
        String yesterdayKey = today.minusDays(1).format(D);

        Mission stateMission = missionRepository.findByCode(CODE_STATE)
                .orElseThrow(() -> new IllegalStateException("Mission not found: " + CODE_STATE));

        Mission dailyMission = missionRepository.findByCode(CODE_DAILY)
                .orElseThrow(() -> new IllegalStateException("Mission not found: " + CODE_DAILY));

        UserMission state = userMissionRepository.findByUserIdAndMissionIdWithLock(userId, stateMission.getId())
                .orElseGet(() -> userMissionRepository.save(UserMission.builder()
                        .userId(userId)
                        .missionId(stateMission.getId())
                        .periodKey("00000000")  // null 불가라서 더미로 시작
                        .progressCount(0)
                        .completedAt(null)
                        .rewardedAt(null)
                        .build()));

        // 오늘 이미 출첵
        if (todayKey.equals(state.getPeriodKey())) {
            return AttendanceCheckInResponse.builder()
                    .success(true)
                    .alreadyCheckedIn(true)
                    .userId(userId)
                    .periodKey(todayKey)
                    .streak(state.getProgressCount())
                    .earnedTickets(0)
                    .bonusTickets(0)
                    .totalEarnedTickets(0)
                    .message("오늘의 출석체크는 완료했습니다! 내일 다시 와주세요")
                    .build();
        }

        // 연속일수 계산
        int nextStreak = yesterdayKey.equals(state.getPeriodKey())
                ? state.getProgressCount() + 1
                : 1;

        // 상태 업데이트
        state.setPeriodKey(todayKey);
        state.setProgressCount(nextStreak);
        state.markCompletedNow();
        state.clearRewardedAt();

        int earnedTickets = 0;
        int bonusTickets = 0;

        // 일일 보상 지급
        int dailyAmount = dailyMission.getRewardTicketAmount();
        if (dailyAmount > 0) {
            String idempotencyKey = "attendance:daily:" + userId + ":" + todayKey;
            ticketService.earnTickets(new TicketEarnReqDto(
                    userId,
                    dailyAmount,
                    "ATTENDANCE_DAILY",
                    "MISSION",
                    String.valueOf(state.getId()),
                    idempotencyKey
            ));
            earnedTickets = dailyAmount;
        }

        // 보너스 지급
        if (BONUS_CODES.containsKey(nextStreak)) {
            Mission bonusMission = missionRepository.findByCode(BONUS_CODES.get(nextStreak))
                    .orElseThrow(() -> new IllegalStateException("Bonus mission not found for day: " + nextStreak));

            int bonusAmount = bonusMission.getRewardTicketAmount();
            if (bonusAmount > 0) {
                String idempotencyKey = "attendance:bonus:" + nextStreak + ":" + userId + ":" + todayKey;
                ticketService.earnTickets(new TicketEarnReqDto(
                        userId,
                        bonusAmount,
                        "ATTENDANCE_BONUS_" + nextStreak,
                        "MISSION",
                        String.valueOf(state.getId()),
                        idempotencyKey
                ));
                bonusTickets = bonusAmount;
                state.markRewardedNow();
            }

            // A 정책: 30일 보너스 지급 즉시 초기화(연속일수 0)
            if (nextStreak == 30) {
                state.setProgressCount(0);
                state.setPeriodKey(todayKey);
            }
        }

        int finalStreak = state.getProgressCount();
        int total = earnedTickets + bonusTickets;

        String msg;
        if (bonusTickets > 0) {
            msg = "출석 완료! 보너스 티켓이 지급되었습니다";
        } else {
            msg = "출석 완료! 티켓이 지급되었습니다";
        }

        return AttendanceCheckInResponse.builder()
                .success(true)
                .alreadyCheckedIn(false)
                .userId(userId)
                .periodKey(todayKey)
                .streak(finalStreak)
                .earnedTickets(earnedTickets)
                .bonusTickets(bonusTickets)
                .totalEarnedTickets(total)
                .message(msg)
                .build();
    }
}
