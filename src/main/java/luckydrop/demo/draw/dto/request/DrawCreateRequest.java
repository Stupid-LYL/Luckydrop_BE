package luckydrop.demo.draw.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DrawCreateRequest {

    //드로우 정보
    @NotBlank
    private final String title;

    private final String description;

    @NotNull
    @Min(1)
    private final Integer ticketCostEntry;

    @NotNull
    @Min(1)
    private final Integer winnerCount;

    @NotNull
    private final LocalDateTime startAt;

    @NotNull
    private final LocalDateTime endAt;

    //상품 정보
    @Valid
    @NotNull
    private final Product product;

    @Getter
    @Builder
    public static class Product {

        @NotBlank
        private final String name;

        @NotBlank
        private final String brand;

        private final String description;

        @PositiveOrZero
        private final Integer retailPrice;

        @Builder.Default
        private final Boolean shippable = true;

        @Valid
        @NotEmpty
        private final List<Image> images;

    }

    @Getter
    @Builder
    public static class Image{

        @NotBlank
        private final String imageUrl;

        //프론트가 안보내면 서버에서 index 이미지로 자동 부여 가능
        private final Integer sortOrder;
    }

}
