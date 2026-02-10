package luckydrop.demo.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import luckydrop.demo.user.entity.Role;
import luckydrop.demo.user.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoResDto {
    private Long id;
    private String nickname;
    private String email;
    private Role role;

    public UserInfoResDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.role = user.getRole();
    }
}
