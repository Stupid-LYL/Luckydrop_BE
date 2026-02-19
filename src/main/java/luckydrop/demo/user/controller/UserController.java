package luckydrop.demo.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.common.auth.JwtTokenProvider;
import luckydrop.demo.common.member.CustomUserPrincipal;
import luckydrop.demo.common.util.CookieUtil;
import luckydrop.demo.user.dto.request.ProfileUpdateReqDto;
import luckydrop.demo.user.dto.request.UserLoginReqDto;
import luckydrop.demo.user.dto.request.UserSaveReqDto;
import luckydrop.demo.user.dto.response.*;
import luckydrop.demo.user.entity.User;
import luckydrop.demo.user.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody UserSaveReqDto userSaveReqDto) {
        User user = userService.create(userSaveReqDto);
        return new ResponseEntity<>(user.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserStatusDto> loginMember(
            @RequestBody @Valid UserLoginReqDto userLoginReqDto,
            HttpServletResponse response) {

        Map<String, Object> authData = userService.login(userLoginReqDto);

        String accessToken = (String) authData.get("accessToken");
        String refreshToken = (String) authData.get("refreshToken");
        UserInfoResDto userInfo = (UserInfoResDto) authData.get("userInfo");

        // Authorization 헤더에 accessToken 실어줌
        // response.addHeader("Authorization", "Bearer " + accessToken);

        // HttpOnly + Secure 쿠키로 저장 (프론트 JS에서 접근 불가)
        cookieUtil.addCookie(response, "accessToken", accessToken, 30 * 60);
        cookieUtil.addCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 14);

        // JSON 응답에는 토큰 안 실음, 사용자 정보만 실음
        UserStatusDto userStatusDto = new UserStatusDto(true, userInfo);
        return new ResponseEntity<>(userStatusDto, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        cookieUtil.getCookie(request, "refreshToken").ifPresent(cookie -> {
            userService.logout(cookie.getValue());
        });

        // 쿠키의 유효기간을 0으로 설정
        cookieUtil.deleteCookie(response, "refreshToken");
        cookieUtil.deleteCookie(response, "accessToken");
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("/token reissue = {}", request.getRequestURI());
        // 쿠키에서 리프레시 토큰을 가져옴
        String refreshToken = cookieUtil.getCookie(request, "refreshToken")
                .map(Cookie::getValue)
                .orElseThrow(() -> new IllegalArgumentException("토큰을 찾지 못했습니다."));

        // 서비스를 호출하여 새로운 액세스 토큰을 발급
        HashMap<String, String> tokenMap = userService.reissue(refreshToken);

        // 응답 헤더에 새로운 액세스 토큰을 추가
        response.addHeader("Authorization", "Bearer " + tokenMap.get("accessToken"));
        // 쿠키에 새로운 리프레시 토큰 추가
        cookieUtil.addCookie(response, "refreshToken", tokenMap.get("refreshToken"), 60 * 60 * 24 * 14);

        return ResponseEntity.ok("액세스 토큰이 성공적으로 재발급되었습니다.");
    }


    @GetMapping("/list")
    public ResponseEntity<?> memberList() {
        List<UserListResDto> dtos = userService.findAll();
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal  CustomUserPrincipal principal,
            @Valid @RequestBody ProfileUpdateReqDto request){
        log.info("프로필 업데이트 요청 : userId={}, request={}", principal.getUser().getId(), request);
        userService.updateProfile(principal.getUser().getId(), request);
        UserInfoResDto updatedUserInfo = userService.getUserInfo(principal.getUser().getId());

        return new ResponseEntity<>(updatedUserInfo, HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailResDto> getUserDetail(
            @AuthenticationPrincipal CustomUserPrincipal principal) {

        Long userId = principal.getUser().getId(); // JWT에서 userId 추출
        UserDetailResDto userDetail = userService.getUserDetail(userId);

        return new ResponseEntity<>(userDetail, HttpStatus.OK);
    }
}
