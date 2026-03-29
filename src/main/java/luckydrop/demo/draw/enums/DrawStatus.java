package luckydrop.demo.draw.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "드로우 상태")
public enum DrawStatus {

    @Schema(description = "생성됨 (시작 전)")
    DRAFT,

    @Schema(description = "진행 중 (응모 가능)")
    ACTIVE,

    @Schema(description = "추첨 중")
    DRAWING,

    @Schema(description = "종료됨")
    CLOSE,

    @Schema(description = "취소됨")
    CANCEL
}
