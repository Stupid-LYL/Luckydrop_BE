package luckydrop.demo.entry.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyEntryListRequest {
    private String search;  // 드로우 제목 검색
    private String status;  // OPEN|LOCKED 등
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private int page = 0;
    private int size = 10;
}
