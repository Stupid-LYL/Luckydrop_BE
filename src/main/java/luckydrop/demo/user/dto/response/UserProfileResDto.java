package luckydrop.demo.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResDto {
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    private String invitationCode;
}
