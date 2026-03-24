package luckydrop.demo.user.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@Builder
public class AdminUserResDto {
    private String id;
    private String nickname;
    private String email;
    private String role;
    private String joinedAt;
    private int currentTickets;
}
