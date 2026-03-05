package luckydrop.demo.mission.enums;

public enum MissionType {
    ATTENDANCE,          // 출석 체크 (일 1회)
    ATTENDANCE_STREAK,   // 연속 출석 보너스(7일/30일 등) - 로직상 구분용
    DAILY,               // 기타 일일 미션
    AD,                  // 광고 미션
    EVENT,               // 이벤트 미션
    INVITE               // 친구 초대 미션
}
