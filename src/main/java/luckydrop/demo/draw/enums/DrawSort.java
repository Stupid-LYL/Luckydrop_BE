package luckydrop.demo.draw.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "드로우 정렬 기준")
public enum DrawSort {

    @Schema(description = "최신 생성순 (createdAt desc)", example = "LATEST")
    LATEST,

    @Schema(description = "응모 시작 최신순 (startAt desc)", example = "STARTED_DESC")
    STARTED_DESC,

    @Schema(description = "마감 임박순 (endAt asc)", example = "ENDING_SOON")
    ENDING_SOON,

    @Schema(description = "최근 종료순 (endAt desc)", example = "ENDED_DESC")
    ENDED_DESC,

    @Schema(description = "북마크 많은순", example = "BOOKMARK")
    BOOKMARK,

    @Schema(description = "참여자 많은순", example = "PARTICIPANT")
    PARTICIPANT
}