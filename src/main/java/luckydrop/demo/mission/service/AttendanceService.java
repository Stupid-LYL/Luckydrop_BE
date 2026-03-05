package luckydrop.demo.mission.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.mission.dto.response.AttendanceCheckInResponse;
import luckydrop.demo.mission.entity.Mission;
import luckydrop.demo.mission.entity.UserMission;
import luckydrop.demo.mission.enums.MissionType;
import luckydrop.demo.mission.enums.UserMissionStatus;
import luckydrop.demo.mission.repository.MissionRepository;
import luckydrop.demo.mission.repository.UserMissionRepository;
import luckydrop.demo.ticket.dto.request.TicketEarnReqDto;
import luckydrop.demo.ticket.dto.response.TicketTransactionResDto;
import luckydrop.demo.ticket.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceService {

    private static final DateTimeFormatter PERIOD_FORMAT = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd
    private static final int BONUS_7_DAYS = 7;
    private static final int BONUS_30_DAYS = 30;

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final TicketService ticketService;

    @Transactional
    public AttendanceCheckInResponse checkIn(Long userId) {
        String periodKey = LocalDate.now().format(PERIOD_FORMAT);

        Mission attendanceMission = missionRepository.findByTypeAndActiveTrue(MissionType.ATTENDANCE)
                .orElseThrow(() -> new IllegalStateException("ATTENDANCE mission not found or inactive"));

        Long missionId = attendanceMission.getId();

        // 1) 오늘 이미 출첵했는지 (유니크 제약이 있지만, 메시지 친절하게 주려고 사전 체크)
        if (userMissionRepository.existsByUserIdAndMissionIdAndPeriodKey(userId, missionId, periodKey)) {
            Optional<UserMission> today = userMissionRepository.findByUserIdAndMissionIdAndPeriodKey(userId, missionId, periodKey);

            int streak = today.map(UserMission::getStreak).orElse(0);
            return AttendanceCheckInResponse.builder()
                    .success(true)
                    .userId(userId)
                    .periodKey(periodKey)
                    .streak(streak)
                    .earnedTickets(0)
                    .bonusTickets(0)
                    .totalEarnedTickets(0)
                    .message("이미 출석 체크가 완료되었습니다.")
                    .build();
        }

        // 2) 어제 기록 기반 streak 계산
        String yesterdayKey = LocalDate.now().minusDays(1).format(PERIOD_FORMAT);

        int streak = 1;
        Optional<UserMission> yesterday = userMissionRepository.findByUserIdAndMissionIdAndPeriodKey(userId, missionId, yesterdayKey);
        if (yesterday.isPresent()) {
            streak = yesterday.get().getStreak() + 1;
        }

        // 3) 오늘 user_mission 저장
        UserMission userMission = UserMission.builder()
                .userId(userId)
                .missionId(missionId)
                .periodKey(periodKey)
                .status(UserMissionStatus.COMPLETED)
                .streak(streak)
                .build();
        userMissionRepository.save(userMission);

        // 4) 기본 보상 지급
        int earned = attendanceMission.getRewardAmount();
        String earnIdempotencyKey = "attendance:" + userId + ":" + periodKey;

        TicketTransactionResDto earnTx = ticketService.earnTickets(
                TicketEarnReqDto.builder()
                        .userId(userId)
                        .amount(earned)
                        .reason("출석 체크 보상")
                        .refType("MISSION")
                        .refId(String.valueOf(userMission.getId()))
                        .idempotencyKey(earnIdempotencyKey)
                        .build()
        );

        // 5) 보너스(7/30) 지급
        int bonus = 0;

        if (streak == BONUS_7_DAYS) {
            int bonusAmount = 5; // ← 여기 보너스 수치는 너희가 정해야 함
            String bonusKey = "attendance_bonus7:" + userId + ":" + periodKey;

            ticketService.earnTickets(
                    TicketEarnReqDto.builder()
                            .userId(userId)
                            .amount(bonusAmount)
                            .reason("연속 출석 7일 보너스")
                            .refType("MISSION")
                            .refId(String.valueOf(userMission.getId()))
                            .idempotencyKey(bonusKey)
                            .build()
            );
            bonus += bonusAmount;
        }

        if (streak == BONUS_30_DAYS) {
            int bonusAmount = 30; // ← 여기 보너스 수치는 너희가 정해야 함
            String bonusKey = "attendance_bonus30:" + userId + ":" + periodKey;

            ticketService.earnTickets(
                    TicketEarnReqDto.builder()
                            .userId(userId)
                            .amount(bonusAmount)
                            .reason("연속 출석 30일 보너스")
                            .refType("MISSION")
                            .refId(String.valueOf(userMission.getId()))
                            .idempotencyKey(bonusKey)
                            .build()
            );
            bonus += bonusAmount;
        }

        log.info("출석 체크 완료 userId={}, periodKey={}, streak={}, earned={}, bonus={}", userId, periodKey, streak, earned, bonus);

        return AttendanceCheckInResponse.builder()
                .success(true)
                .userId(userId)
                .periodKey(periodKey) // 오늘 날짜 yyyyMMdd
                .streak(streak)
                .earnedTickets(earned)
                .bonusTickets(bonus)
                .totalEarnedTickets(earned + bonus)
                .message("출석 체크 완료")
                .build();
    }
}
