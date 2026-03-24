package luckydrop.demo.draw.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminDrawForceCancelRequest(

        @NotBlank(message = "취소 사유 코드는 필수입니다.")
        String reasonCode,

        @Size(max = 200, message = "취소 상세 사유는 200자 이하여야 합니다.")
        String reasonDetail

) {
}
