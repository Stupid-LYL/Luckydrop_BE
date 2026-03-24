package luckydrop.demo.draw.dto.response;

public record HostWinnerInfoResponse(
        Long winnerUserId,
        String name,
        String nickname,
        String phone,
        String address
) {
}
