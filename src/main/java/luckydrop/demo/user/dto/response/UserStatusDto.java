package luckydrop.demo.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStatusDto {

        // 인증 여부
        private boolean authenticated;
        // 사용자 정보
        private UserInfoResDto user;
}
