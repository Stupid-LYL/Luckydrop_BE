package luckydrop.demo.email.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailReqDto {
    @Email
    private String email;
}
