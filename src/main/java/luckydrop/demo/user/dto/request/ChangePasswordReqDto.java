package luckydrop.demo.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordReqDto {
    @NotBlank @Size(min=8)
    private String currentPassword;
    @NotBlank
    @Size(min=8)
    private String newPassword;
}
