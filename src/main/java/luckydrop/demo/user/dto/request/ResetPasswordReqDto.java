package luckydrop.demo.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordReqDto {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String authNum;
    @NotBlank
    private String newPassword;
}
