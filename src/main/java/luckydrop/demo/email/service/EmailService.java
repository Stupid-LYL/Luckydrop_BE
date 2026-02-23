package luckydrop.demo.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.Year;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String senderEmail;

    private int authNumber;

    // 6자리 랜덤 인증번호 생성
    public void makeRandomNum() {
        Random r = new Random();
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            randomNumber.append(r.nextInt(10));
        }
        authNumber = Integer.parseInt(randomNumber.toString());
    }

    // ✅ 회원가입 이메일 (키 구분)
    public String joinEmail(String email) {
        makeRandomNum();
        String title = "[Luckydrop] - 회원가입 이메일 인증";
        String content = getJoinEmailContent();

        log.info("회원가입 인증번호 생성: email={}, code={}", email, authNumber);
        mailSend(senderEmail, email, title, content);

        // 키 구분: "auth:join:email"
        String redisKey = "auth:join:" + email;
        redisTemplate.opsForValue().set(redisKey, Integer.toString(authNumber), 180, TimeUnit.SECONDS);
        log.info("회원가입 코드 Redis 저장: key={}", redisKey);

        return Integer.toString(authNumber);
    }

    // ✅ 비밀번호 재설정 이메일 (키 구분)
    public String sendPasswordResetEmail(String email) {
        makeRandomNum();
        String title = "[Luckydrop] - 비밀번호 재설정 인증";
        String content = getPasswordResetEmailContent();

        log.info("비밀번호 재설정 코드 생성: email={}, code={}", email, authNumber);
        mailSend(senderEmail, email, title, content);

        // 키 구분: "auth:reset:email"
        String redisKey = "auth:reset:" + email;
        redisTemplate.opsForValue().set(redisKey, Integer.toString(authNumber), 180, TimeUnit.SECONDS);
        log.info("재설정 코드 Redis 저장: key={}", redisKey);

        return Integer.toString(authNumber);
    }

    // ✅ 회원가입 인증 검증
    public boolean checkJoinAuthNum(String email, String authNum) {
        String redisKey = "auth:join:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        log.info("회원가입 인증 확인: email={}, input={}, stored={}", email, authNum, storedCode);

        boolean isValid = Objects.equals(storedCode, authNum);
        if (isValid) {
            redisTemplate.delete(redisKey);  // 사용 후 즉시 삭제
        }
        return isValid;
    }

    // ✅ 재설정 인증 검증
    public boolean checkResetAuthNum(String email, String authNum) {
        String redisKey = "auth:reset:" + email;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        log.info("재설정 인증 확인: email={}, input={}, stored={}", email, authNum, storedCode);

        boolean isValid = Objects.equals(storedCode, authNum);
        if (isValid) {
            redisTemplate.delete(redisKey);  // 사용 후 즉시 삭제
        }
        return isValid;
    }
    // HTML 템플릿: 회원가입용
    private String getJoinEmailContent() {
        return "<!DOCTYPE html>" +
                "<html lang='ko'>" +
                "<head>" +
                "  <meta charset='UTF-8' />" +
                "  <title>[Luckydrop] 이메일 인증</title>" +
                "  <style>" +
                "    body { margin:0; padding:0; background:#f5f5f7; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif; }" +
                "    .wrapper { width:100%; padding:24px 0; }" +
                "    .mail-container { max-width:520px; margin:0 auto; background:#ffffff; border-radius:16px;" +
                "      box-shadow:0 10px 30px rgba(0,0,0,0.08); overflow:hidden; }" +
                "    .header { padding:20px 32px; display:flex; align-items:center; gap:10px; }" +
                "    .logo-circle { width:32px; height:32px; border-radius:999px;" +
                "      background:linear-gradient(135deg,#8b5cff,#ff2fb9); display:flex; align-items:center; justify-content:center; color:#fff; font-weight:700; }" +
                "    .brand { font-size:20px; font-weight:700; color:#222; }" +
                "    .hero { padding:0 32px 4px; color:#888; font-size:13px; }" +
                "    .title { padding:4px 32px 0; font-size:22px; font-weight:700; color:#111; }" +
                "    .divider { margin:18px 32px 0; border-top:1px solid #eee; }" +
                "    .content { padding:18px 32px 24px; font-size:14px; color:#333; line-height:1.6; }" +
                "    .code-box { margin:16px 0; padding:14px 18px; border-radius:12px;" +
                "      background:linear-gradient(135deg,#8b5cff,#ff2fb9); color:#fff; font-size:24px; font-weight:700;" +
                "      letter-spacing:4px; text-align:center; }" +
                "    .btn { display:inline-block; margin-top:12px; padding:12px 24px; border-radius:999px;" +
                "      background:linear-gradient(135deg,#8b5cff,#ff2fb9); color:#fff; font-size:14px; font-weight:600; text-decoration:none; }" +
                "    .meta { margin-top:18px; font-size:12px; color:#888; }" +
                "    .footer { padding:14px 32px 22px; font-size:11px; color:#aaa; text-align:center; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='wrapper'>" +
                "    <div class='mail-container'>" +
                "      <div class='header'>" +
                "        <div class='logo-circle'>L</div>" +
                "        <div class='brand'>Luckydrop</div>" +
                "      </div>" +
                "      <p class='hero'>럭키 드로우에 참여하기 위한 이메일 인증 안내입니다.</p>" +
                "      <h1 class='title'>이메일 인증 코드를 확인해주세요</h1>" +
                "      <div class='divider'></div>" +
                "      <div class='content'>" +
                "        <p>안녕하세요, Luckydrop을 이용해 주셔서 감사합니다.<br/>" +
                "        아래의 인증 코드를 회원가입 화면에 입력하면 이메일 인증이 완료됩니다.</p>" +
                "        <div class='code-box'>" + authNumber + "</div>" +
                "        <p class='meta'>· 인증 코드는 발송 시점 기준으로 3분 동안만 유효합니다.<br/>" +
                "        · 본인이 요청하지 않은 메일이라면 이 메일은 무시하셔도 됩니다.</p>" +
                "        <a class='btn' href='http://localhost:5173'>Luckydrop 바로가기</a>" +
                "      </div>" +
                "      <div class='footer'>" +
                "        본 메일은 발신 전용입니다. 문의는 서비스 내 고객센터를 이용해주세요.<br/>" +
                "        © " + Year.now() + " Luckydrop. All rights reserved." +
                "      </div>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }

    // HTML 템플릿: 비밀번호 재설정용
    private String getPasswordResetEmailContent() {
        return getJoinEmailContent()  // 공통 스타일 재사용
                .replace("<h1 class='title'>이메일 인증 코드를 확인해주세요</h1>",
                        "<h1 class='title'>비밀번호 재설정 인증 코드입니다</h1>")
                .replace("아래의 인증 코드를 회원가입 화면에 입력하면 이메일 인증이 완료됩니다.",
                        "아래 인증 코드를 입력하면 비밀번호 재설정이 가능합니다.")
                .replace("럭키 드로우에 참여하기 위한 이메일 인증 안내입니다.",
                        "비밀번호 재설정을 위한 인증 코드입니다.")
                .replace("Luckydrop 바로가기", "재설정 페이지로 이동");
    }

    // 이메일 전송
    public void mailSend(String setFrom, String toMail, String title, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(setFrom, "Luckydrop", "UTF-8"));
            helper.setTo(toMail);
            helper.setSubject(title);
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    // 인증번호 검증 (재사용)
    public boolean checkAuthNum(String email, String authNum) {
        String code = redisTemplate.opsForValue().get(email);
        return Objects.equals(code, authNum);
    }

    // 재설정 코드 검증 (기존 재사용)
    public boolean verifyResetCode(String email, String authNum) {
        return checkAuthNum(email, authNum);
    }

    // Redis 코드 삭제 (한 번 사용 후)
    public void deleteResetCode(String email) {
        redisTemplate.delete(email);
    }
}
