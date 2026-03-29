package luckydrop.demo.entry.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "드로우 응모 요청")
public class DrawEntryRequest {

    @Schema(description = "응모 횟수", example = "1")
    @Min(1)
    @Max(1000)
    @NotNull
    private Integer count;
}
