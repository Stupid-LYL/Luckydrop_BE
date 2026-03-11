package luckydrop.demo.draw.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.draw.bookmark.service.DrawBookmarkService;
import luckydrop.demo.draw.dto.request.DrawCreateRequest;
import luckydrop.demo.draw.dto.request.DrawUpdateRequest;
import luckydrop.demo.draw.dto.response.DrawDetailResponse;
import luckydrop.demo.draw.dto.response.DrawWinnerResponse;
import luckydrop.demo.draw.dto.response.MyWinResponse;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.entity.DrawEntrySummary;
import luckydrop.demo.draw.entity.DrawWinner;
import luckydrop.demo.draw.enums.DrawStatus;
import luckydrop.demo.draw.inventory.entity.Inventory;
import luckydrop.demo.draw.inventory.entity.InventoryImage;
import luckydrop.demo.entry.repository.DrawEntrySummaryRepository;
import luckydrop.demo.draw.repository.DrawRepository;
import luckydrop.demo.draw.inventory.repository.InventoryImageRepository;
import luckydrop.demo.draw.inventory.repository.InventoryRepository;
import luckydrop.demo.draw.repository.DrawWinnerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrawService {

    private final DrawWinnerRepository drawWinnerRepository;
    private final DrawRepository drawRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryImageRepository inventoryImageRepository;
    private final DrawEntrySummaryRepository drawEntrySummaryRepository;
    private final DrawBookmarkService drawBookmarkService;

    @Transactional
    public DrawDetailResponse updateDraw(Long drawId, Long requesterUserId, DrawUpdateRequest request) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() ->  new IllegalArgumentException("드로우가 존재하지 않습니다."));

        // 1. status == ACTIVE 응모 시작한 드로우는 수정x
        if (draw.getStatus() == DrawStatus.ACTIVE) {
            throw new IllegalArgumentException("응모가 시작된 드로우는 수정할 수 없습니다.");
        }

        // 2. now < startAt 현재 시간이 시작 시간을 지났을 경우 수정x
        LocalDateTime now = LocalDateTime.now();
        if (!now.isBefore(draw.getStartAt())) {
            throw new IllegalArgumentException("응모 시작 시간이 지난 드로우는 수정할 수 없습니다.");
        }

        // 3. 응모 0명인지 아닌지(entry_summary 기준)
        long totalEntries = drawEntrySummaryRepository.countParticipants(drawId);
        if (totalEntries > 0) {
            throw new IllegalArgumentException("응모자가 발생한 드로우는 수정할 수 없습니다.");
        }

        if (!draw.getUserId().equals(requesterUserId)) {
            throw new AccessDeniedException("해당 드로우를 생성한 HOST만 수정할 수 있습니다.");
        }

        // 4. description 수정
        if (request.hasDescription()) {
            draw.changeDescription(request.getDescription());
        }

        // 5. endAt 수정 (최대 1회 + 시간 연장만 가능)
        if (request.hasEndAt()) {
            if (draw.isEndAtChanged()) {
                throw new IllegalArgumentException("종료 시간은 최대 1회만 수정할 수 있습니다.");
            }

            LocalDateTime newEndAt = request.getEndAt();

            if (!newEndAt.isAfter(draw.getStartAt())) {
                throw new IllegalArgumentException("종료 시간은 시작 시간 이후여야 합니다.");
            }

            if (!newEndAt.isAfter(draw.getEndAt())) {
                throw new IllegalArgumentException("종료 시간은 기존 종료 시간보다 뒤로만 연장할 수 있습니다.");
            }

            draw.changeEndAt(newEndAt);
            draw.markEndAtChanged();
        }

        boolean isBookmarked = drawBookmarkService.isBookmarked(requesterUserId, drawId);
        long bookmarkCount = drawBookmarkService.getBookmarkCount(drawId);

        return DrawDetailResponse.from(draw, isBookmarked, bookmarkCount, totalEntries, 0);
    }


    @Transactional
    public Long createDraw(Long userId, DrawCreateRequest req) {

        // 기본 검증
        validateDrawTime(req.getStartAt(), req.getEndAt());
        validateCounts(req.getTicketCostEntry(), req.getWinnerCount());
        validateImages(req);

        // Inventory 생성/저장 (드로우 생성 시 상품 정보도 같이 저장)
        DrawCreateRequest.Product p = req.getProduct();

        Inventory inventory = Inventory.builder()
                .name(p.getName())
                .brand(p.getBrand())
                .description(p.getDescription())
                .retailPrice(p.getRetailPrice())
                .shippable(p.getShippable())
                .build();

        inventoryRepository.save(inventory);

        // inventoryImages 생성/저장 (sortOrder 없으면 자동부여)
        List<InventoryImage> images = new ArrayList<>();
        int autoOrder = 1;

        for (DrawCreateRequest.Image img : p.getImages()) {
            Integer sortOrder = (img.getSortOrder() != null) ? img.getSortOrder() : autoOrder++;

            InventoryImage image = InventoryImage.builder()
                    .inventory(inventory)
                    .imageUrl(img.getImageUrl())
                    .sortOrder(sortOrder)
                    .build();

            images.add(image);
            inventory.getImages().add(image);
        }
        inventoryImageRepository.saveAll(images);

        //Draw 생성/저장 (inventory 1개당 draw 1개 강제는 DB UNIQUE 가 최종 보루)
        Draw draw = Draw.builder()
                .userId(userId)
                .inventory(inventory)
                .title(req.getTitle())
                .description(req.getDescription())
                .ticketCostEntry(req.getTicketCostEntry())
                .winnerCount(req.getWinnerCount())
                .startAt(req.getStartAt())
                .endAt(req.getEndAt())
                .status(DrawStatus.DRAFT)
                .build();

        //더블 클릭해서 중복 드로우가 될 것을 방지
        try {
            drawRepository.save(draw);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("이미 이 상품으로 생성된 드로우가 있어요" ,e);
        }

        System.out.println("드로우를 저장하였습니다.");
        return draw.getId();
    }



    public List<MyWinResponse> getMyWins(Long userId) {
        return drawWinnerRepository.findByUserId(userId).stream()
                .map(w -> MyWinResponse.builder()
                        .drawId(w.getDrawId())
                        .build())
                .toList();
    }

    @Transactional
    public DrawWinnerResponse getWinner(Long drawId) {

        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new IllegalArgumentException("드로우가 존재하지 않습니다."));

        // 정책: CLOSED일 때만 공개
        if (draw.getStatus() != DrawStatus.CLOSE) {
            throw new IllegalArgumentException("아직 추첨 결과가 공개되지 않았습니다.");
        }

        List<Long> winnersUserIds = drawWinnerRepository.findByDrawId(drawId).stream()
                .map(DrawWinner::getUserId)
                .toList();

        return DrawWinnerResponse.builder()
                .drawId(drawId)
                .winnersUserIds(winnersUserIds)
                .build();
    }

    @Transactional
    public List<DrawWinner> drawWinner(Long drawId) {

        // DRAWING이면 CLOSED로 바꾼다"를 원자적으로 실행
        int updated = drawRepository.updateDrawingToClosed(drawId);
        if (updated == 0) {
            throw new IllegalArgumentException("추첨을 진행할 수 없습니다. (상태가 DARWING이 아니거나 이미 처리됨)");
        }

        List<DrawEntrySummary.ParticipantWeight> candidates = drawEntrySummaryRepository.findWeights(drawId);
        if (candidates.isEmpty()) {
            return List.of();
        }

        // winnerCount는 Draw에서 읽어야 하나까 draw 조회 1번 필요
        Draw draw = drawRepository.findByIdForUpdate(drawId)
                .orElseThrow(() -> new IllegalArgumentException("드로우가 존재하지 않습니다."));

        if (drawWinnerRepository.existsByDrawId(drawId)) {
            throw new IllegalArgumentException("이미 추첨이 완료된 드로우입니다.");
        }

        int winnerCount = draw.getWinnerCount();
        if (winnerCount <= 0) {
            throw new IllegalArgumentException("winnerCount가 올바르지 않습니다.");
        }

        int k  = Math.min(winnerCount, candidates.size());

        List<Long> winnerUserIds = pickWeightedWinners(candidates, k);

        log.info("[Draw] drawId={} winners={}", drawId, winnerUserIds);

        List<DrawWinner> winners = new ArrayList<>();
        for (Long userId : winnerUserIds) {
            winners.add(DrawWinner.builder()
                    .drawId(drawId)
                    .userId(userId)
                    .build());
        }
        drawWinnerRepository.saveAll(winners);

        return winners;
    }

    // 당첨자 추첨 로직
    private List<Long> pickWeightedWinners(List<DrawEntrySummary.ParticipantWeight> candidates, int k) {

        List<Scored> scored = new ArrayList<>(candidates.size());

        for (DrawEntrySummary.ParticipantWeight c : candidates) {
            long w = c.getEntryCount();
            if (w <= 0) continue;

            double u = Math.max(Math.random(), 1e-12);
            double key = -Math.log(u) / (double) w;

            scored.add(new Scored(c.getUserId(), key));
        }

        scored.sort(Comparator.comparingDouble(Scored::key));

        return scored.stream()
                .limit(k)
                .map(Scored::userId)
                .toList();
    }

    private record Scored(Long userId, double key) {}


    private void validateDrawTime(LocalDateTime startAt, LocalDateTime endAt) {

        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("시작시간/종료시간 입력은 필수");
        }
        if (!startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("시간 설정을 다시 해주세요. 시작 시간은 현재보다 미래여야 합니다.");
        }
    }


    private void validateCounts(Integer ticketCostEntry, Integer winnerCount) {

        if (ticketCostEntry == null || ticketCostEntry < 1) {
            throw new IllegalArgumentException("필요 티켓 수 입력은 1장 이상");
        }

        if (winnerCount == null || winnerCount < 1) {
            throw new IllegalArgumentException("당첨자 수는 1명 이상");
        }
    }


    private void validateImages(DrawCreateRequest req) {

        if (req.getProduct() == null || req.getProduct().getImages() == null || req.getProduct().getImages().isEmpty()) {
            throw new IllegalArgumentException("상품 이미지는 최소 1개 필요");
        }
        for (DrawCreateRequest.Image img : req.getProduct().getImages()) {
            if (img.getImageUrl() == null || img.getImageUrl().isBlank()) {
                throw new IllegalArgumentException("imageUrl은 비어있으면 안됨");
            }
        }
    }
}
