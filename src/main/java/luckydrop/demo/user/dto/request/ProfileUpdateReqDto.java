package luckydrop.demo.user.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ProfileUpdateReqDto {
    @Size(min = 2, message = "닉네임은 최소 2자 이상이어야 합니다.")
    private String nickname;

    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$",
            message = "올바른 전화번호 형식이 아닙니다.")
    private String phone;

    private String address;

    @Builder
    public ProfileUpdateReqDto(String nickname, String phone, String address) {
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
    }
}
