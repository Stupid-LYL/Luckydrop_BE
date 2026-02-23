package luckydrop.demo.email.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import luckydrop.demo.email.dto.EmailCheckDto;
import luckydrop.demo.email.dto.EmailReqDto;
import luckydrop.demo.email.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    // 인증번호 발송
    @PostMapping("/signup/email")
    public ResponseEntity<Map<String, String>> mailSend(
            @RequestBody @Valid EmailReqDto emailRequestDto) {
        String code = emailService.joinEmail(emailRequestDto.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        return ResponseEntity.ok(response);
    }

    // 인증번호 확인
    @PostMapping("/signup/emailAuth")
    public ResponseEntity<String> authCheck(@RequestBody @Valid EmailCheckDto emailCheckDto) {
        boolean checked = emailService.checkJoinAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());
        if (checked) {
            return ResponseEntity.ok("인증 성공");
        }
        throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
    }
}

