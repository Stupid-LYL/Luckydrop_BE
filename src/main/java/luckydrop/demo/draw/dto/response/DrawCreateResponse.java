package luckydrop.demo.draw.dto.response;

import lombok.Getter;

@Getter
public class DrawCreateResponse {

    private final Long drawId;

    public DrawCreateResponse(Long drawId) {
        this.drawId = drawId;
    }

    public static DrawCreateResponse of(Long drawId) {
        return new DrawCreateResponse(drawId);
    }
}
