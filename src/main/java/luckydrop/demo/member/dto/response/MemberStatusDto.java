package luckydrop.demo.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemberStatusDto {

        // 인증 여부
        private boolean authenticated;
        // 사용자 정보
        private UserInfoResDto user;
}
