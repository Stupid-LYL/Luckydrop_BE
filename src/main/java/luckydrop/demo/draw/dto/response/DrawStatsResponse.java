package luckydrop.demo.draw.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "드로우 목록 상단 통계 응답")
public record DrawStatsResponse(

        @Schema(description = "전체 드로우 수", example = "128")
        long totalDrawCount,

        @Schema(description = "진행중 드로우 수", example = "17")
        long activeDrawCount,

        @Schema(description = "총 응모 수", example = "3482")
        long totalEntryCount
) {
}
