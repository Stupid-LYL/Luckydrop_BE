package luckydrop.demo.entry.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class DrawEntryRequest {
    @Min(1)
    @Max(1000)
    @NotNull
    private Integer count;
}
