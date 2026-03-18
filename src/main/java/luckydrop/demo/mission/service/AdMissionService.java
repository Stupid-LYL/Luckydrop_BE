package luckydrop.demo.mission.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.mission.entity.Mission;
import luckydrop.demo.mission.entity.UserMission;
import luckydrop.demo.mission.enums.MissionType;
import luckydrop.demo.mission.repository.MissionRepository;
import luckydrop.demo.mission.repository.UserMissionRepository;
import luckydrop.demo.ticket.dto.request.TicketEarnReqDto;
import luckydrop.demo.ticket.repository.TicketLedgerRepository;
import luckydrop.demo.ticket.service.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdMissionService {

    private static final DateTimeFormatter D = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    private final MissionRepository missionRepository;
    private final UserMissionRepository userMissionRepository;
    private final TicketService ticketService;
    private final TicketLedgerRepository ticketLedgerRepository;

    @Transactional
    public void completeAdMission(Long userId) {

        // 광고 미션 조회
        Mission mission = missionRepository
                .findByMissionTypeAndActiveTrue(MissionType.AD)
                .orElseThrow(() -> new IllegalArgumentException("광고 미션이 존재하지 않습니다."));

        // 프로젝트 기존 스타일과 맞춤: yyyyMMdd
        String periodKey = LocalDate.now().format(D);

        // 이미 수행했는지 확인
        Optional<UserMission> existing = userMissionRepository
                .findByUserIdAndMissionIdAndPeriodKey(userId, mission.getId(), periodKey);

        if (existing.isPresent()) {
            return;
        }

        String idempotencyKey = "AD:" + userId + ":" + periodKey;

        // 이미 원장에 기록된 경우도 중복 처리로 보고 종료
        if (ticketLedgerRepository.existsByIdempotencyKey(idempotencyKey)) {
            return;
        }

        // UserMission 생성
        UserMission userMission = UserMission.builder()
                .userId(userId)
                .missionId(mission.getId())
                .periodKey(periodKey)
                .progressCount(1)
                .completedAt(java.time.LocalDateTime.now())
                .rewardedAt(java.time.LocalDateTime.now())
                .build();

        userMissionRepository.save(userMission);

        // 티켓 지급
        TicketEarnReqDto req = TicketEarnReqDto.builder()
                .userId(userId)
                .amount(mission.getRewardTicketAmount())
                .reason("광고 미션 보상")
                .refType("MISSION_AD")
                .refId(mission.getId())
                .idempotencyKey(idempotencyKey)
                .build();

        ticketService.earnTickets(req);
    }
}