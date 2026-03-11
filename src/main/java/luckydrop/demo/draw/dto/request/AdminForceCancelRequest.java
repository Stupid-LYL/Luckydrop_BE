package luckydrop.demo.draw.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AdminForceCancelRequest {

    @NotBlank
    @Size(max = 255)
    private String cancelReasonDetail;
}
