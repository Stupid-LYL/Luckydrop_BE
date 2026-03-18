package luckydrop.demo.draw.enums;

public enum DrawSort {

    //공통
    LATEST, // createdAt desc

    // UPCOMING
    BOOKMARK,

    // ONGOING
    STARTED_DESC, // startAt desc (기본)
    PARTICIPANT, // 참여자 많은 순
    ENDING_SOON, // 마감 임박 순

    // CLOSED
    ENDED_DESC
}
