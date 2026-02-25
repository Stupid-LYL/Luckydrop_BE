package luckydrop.demo.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordReqDto {
    @Email @NotBlank
    private String email;
}
