package luckydrop.demo.email.dto;

import lombok.Data;

@Data
public class EmailCheckDto {
    private String email;
    private String authNum;
}
