package luckydrop.demo.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import luckydrop.demo.member.entity.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResDto {
    private Long id;
    private String nickname;
    private String email;
    private Role role;
}
