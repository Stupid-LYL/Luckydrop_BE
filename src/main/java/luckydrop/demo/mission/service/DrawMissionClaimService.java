package luckydrop.demo.mission.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.mission.entity.Mission;
import luckydrop.demo.mission.entity.UserMission;
import luckydrop.demo.mission.repository.MissionRepository;
import luckydrop.demo.mission.repository.UserMissionRepository;
import luckydrop.demo.ticket.dto.request.TicketEarnReqDto;
import luckydrop.demo.ticket.service.TicketService;
import luckydrop.demo.entry.repository.DrawEntrySummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DrawMissionClaimService {

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final TicketService ticketService;
    private final DrawEntrySummaryRepository drawEntrySummaryRepository;

    @Transactional
    public void claimFirstDrawMission(Long userId) {

        // 응모 이력이 있는지 확인
        boolean hasAnyEntry = drawEntrySummaryRepository.existsByUserId(userId);

        if (!hasAnyEntry) {
            throw new IllegalStateException("드로우 응모 이력이 없습니다.");
        }

        // 미션 조회
        Mission mission = missionRepository.findByCode("DRAW_FIRST")
                .orElseThrow(() -> new IllegalStateException("미션 없음"));

        // 이미 보상 받았는지 확인 (1회성 미션)
        boolean alreadyClaimed = userMissionRepository
                .findByUserIdAndMissionId(userId, mission.getId())
                .isPresent();

        if (alreadyClaimed) {
            throw new IllegalStateException("이미 보상을 받았습니다.");
        }

        // 기록 생성
        UserMission userMission = UserMission.builder()
                .userId(userId)
                .missionId(mission.getId())
                .periodKey("FIRST")
                .progressCount(1)
                .build();

        userMissionRepository.save(userMission);

        // 티켓 지급
        ticketService.earnTickets(
                TicketEarnReqDto.builder()
                        .userId(userId)
                        .amount(mission.getRewardTicketAmount())
                        .reason("DRAW_FIRST")
                        .refType("MISSION")
                        .refId(mission.getId())
                        .idempotencyKey("DRAW_FIRST:" + userId)
                        .build()
        );
    }

    @Transactional
    public void claimDailyDrawMission(Long userId) {

        // 오늘 시작/끝 시간
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        // 오늘 응모했는지 확인
        boolean hasEntryToday = drawEntrySummaryRepository
                .existsByUserIdAndCreatedAtBetween(userId, start, end);

        if (!hasEntryToday) {
            throw new IllegalStateException("오늘 드로우 응모 이력이 없습니다.");
        }

        // 미션 조회
        Mission mission = missionRepository.findByCode("DRAW_DAILY")
                .orElseThrow(() -> new IllegalStateException("미션 없음"));

        // 이미 보상 받았는지 확인
        boolean alreadyClaimed = userMissionRepository
                .existsByUserIdAndMissionIdAndPeriodKey(
                        userId,
                        mission.getId(),
                        today.toString()
                );

        if (alreadyClaimed) {
            throw new IllegalStateException("이미 보상을 받았습니다.");
        }

        // 기록 생성
        UserMission userMission = UserMission.builder()
                .userId(userId)
                .missionId(mission.getId())
                .periodKey(today.toString())
                .progressCount(1)
                .build();

        userMissionRepository.save(userMission);

        // 티켓 지급
        ticketService.earnTickets(
                TicketEarnReqDto.builder()
                        .userId(userId)
                        .amount(mission.getRewardTicketAmount())
                        .reason("DRAW_DAILY")
                        .refType("MISSION")
                        .refId(mission.getId())
                        .idempotencyKey("DRAW_DAILY:" + userId + ":" + today)
                        .build()
        );
    }
}