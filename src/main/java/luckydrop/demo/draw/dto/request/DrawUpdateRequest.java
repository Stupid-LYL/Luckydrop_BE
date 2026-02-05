package luckydrop.demo.draw.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DrawUpdateRequest {

    @Size(max = 2000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasEndAt() {
        return endAt != null;
    }
}
