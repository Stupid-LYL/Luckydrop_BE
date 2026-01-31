package luckydrop.demo.draw.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.draw.dto.request.DrawCreateRequest;
import luckydrop.demo.draw.entity.Draw;
import luckydrop.demo.draw.enums.DrawStatus;
import luckydrop.demo.draw.inventory.entity.Inventory;
import luckydrop.demo.draw.inventory.entity.InventoryImage;
import luckydrop.demo.draw.repository.DrawRepository;
import luckydrop.demo.draw.inventory.InventoryImageRepository;
import luckydrop.demo.draw.inventory.InventoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DrawService {

    private final DrawRepository drawRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryImageRepository inventoryImageRepository;

    @Transactional
    public Long createDraw(DrawCreateRequest req) {
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
                .inventory(inventory)
                .title(req.getTitle())
                .description(req.getDescription())
                .ticketCostEntry(req.getTicketCostEntry())
                .winnerCount(req.getWinnerCount())
                .startAt(req.getStartAt())
                .endAt(req.getEndAt())
                .status(DrawStatus.ACTIVE)
                .build();

        //더블 클릭해서 중복 드로우가 될 것을 방지
        try {
            drawRepository.save(draw);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("이미 이 상품으로 생성된 드로우가 있어요" ,e);
        }

        return draw.getId();
    }


    private void validateDrawTime(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("시작시간/종료시간 입력은 필수");
        }
        if (!startAt.isBefore(endAt)) {
            throw new IllegalArgumentException("시간 설정을 다시 해주세요");
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
