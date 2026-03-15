package luckydrop.demo.draw.enums;

public enum DrawSort {
    LATEST,         // createdAt desc
    STARTED_DESC,   // startAt desc
    ENDING_SOON,    // endAt asc
    ENDED_DESC,     // endAt desc
    BOOKMARK,       // bookmark 많은 순
    PARTICIPANT     // 응모자 많은 순
}