package luckydrop.demo.draw.inventory.dto;

import java.util.List;

public record ImageUploadResponse(
        List<String> imageUrls
) {
}