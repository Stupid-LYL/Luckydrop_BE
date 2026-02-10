package luckydrop.demo.user.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.common.auth.JwtTokenProvider;
import luckydrop.demo.user.dto.request.ProfileUpdateReqDto;
import luckydrop.demo.user.dto.request.UserLoginReqDto;
import luckydrop.demo.user.dto.request.UserSaveReqDto;
import luckydrop.demo.user.dto.response.UserListResDto;
import luckydrop.demo.user.dto.response.UserInfoResDto;
import luckydrop.demo.user.entity.User;
import luckydrop.demo.user.entity.RefreshToken;
import luckydrop.demo.user.repository.UserRepository;
import luckydrop.demo.user.repository.RefreshTokenRepository;
import luckydrop.demo.ticket.entity.TicketWallet;
import luckydrop.demo.ticket.repository.TicketWalletRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TicketWalletRepository ticketWalletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public User create(UserSaveReqDto userSaveReqDto) {
        // 이미 가입되어 있는 이메일 검증
        if(userRepository.findByEmail(userSaveReqDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        User newUser = User.builder()
                .name(userSaveReqDto.getName())
                .nickname(userSaveReqDto.getNickname())
                .phone(userSaveReqDto.getPhone())
                .address(userSaveReqDto.getAddress())
                .email(userSaveReqDto.getEmail())
                .password(passwordEncoder.encode(userSaveReqDto.getPassword()))
                .build();

        User user = userRepository.save(newUser);

        TicketWallet ticketWallet = TicketWallet.builder()
                .user(newUser)
                .balance(0)
                .build();

        ticketWalletRepository.save(ticketWallet);

        return user;
    }

    public Map<String, Object> login(UserLoginReqDto userLoginReqDto) {
        String email = userLoginReqDto.getEmail();
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(userLoginReqDto.getPassword(), findUser.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(findUser.getEmail(), String.valueOf(findUser.getRole()));

        // 리프레시 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(findUser.getEmail(), String.valueOf(findUser.getRole()));

        // 리프레시 토큰의 만료 시간
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtTokenProvider.REFRESH_TOKEN_VALIDITY_SECONDS / 1000);

        // 토큰 빌더 엔티티 생성
        RefreshToken newRefreshToken = RefreshToken.builder()
                .user(findUser)
                .token(refreshToken)
                .expiredAt(refreshTokenExpireTime)
                .build();

        // 토큰 저장
        refreshTokenRepository.save(newRefreshToken);

        // 사용자 정보 생성
        UserInfoResDto userInfo = new UserInfoResDto(
                findUser.getId(),
                findUser.getNickname(),
                findUser.getEmail(),
                findUser.getRole()

        );

        Map<String, Object> authData = new HashMap<>();
        authData.put("accessToken", accessToken);
        authData.put("refreshToken", refreshToken);
        authData.put("userInfo", userInfo);

        log.info("로그인 성공 및 토큰 생성. email = {}", email);
        return authData;
    }

    public HashMap<String, String> reissue(String refreshToken) {
        // 리프레시 토큰 유효성 검증 (만료, 위조 등)
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("토큰이 유효하지 않습니다. ");
        }

        // DB에 저장된 토큰인지, 그리고 만료되지 않았는지 확인
        RefreshToken findRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 존재하지 않습니다."));

        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.delete(findRefreshToken);

        // 토큰에서 사용자 정보(loginId, role) 추출 및 검증(위에서 진행하긴 했음)
        String email = jwtTokenProvider.getEmail(refreshToken);
        String role = jwtTokenProvider.getRole(refreshToken);

        // 리프레시 토큰 엔티티 저장하기 위해 추출
        User findUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(email, role);
        // 새로운 리프레시 토큰 생성
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email, role);
        // 만료시간(DB저장 용)
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtTokenProvider.REFRESH_TOKEN_VALIDITY_SECONDS / 1000);

        // 토큰 빌더 엔티티 생성
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .user(findUser)
                .token(newRefreshToken)
                .expiredAt(refreshTokenExpireTime)
                .build();
        // DB 저장
        refreshTokenRepository.save(newRefreshTokenEntity);

        log.info("액세스 토큰을 재발급했습니다. user: {}", email);
        log.info("리프레시 토큰을 재발급했습니다. user: {}", email);

        // 컨트롤러 리턴용 맵
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("accessToken", newAccessToken);
        tokenMap.put("refreshToken", newRefreshToken);
        // 컨트롤러에 새로운 액세스, 리프레시 토큰 반환
        return tokenMap;
    }

    public void logout(String refreshToken) {
        // 토큰 찾고 있으면 제거
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            refreshTokenRepository.delete(token);
            log.info("리프레시 토큰을 DB에서 제거했습니다. user: {}", token.getUser().getEmail());
        });
    }

    public List<UserListResDto> findAll(){
        List<User> users = userRepository.findAll();
        List<UserListResDto> userListResDtos = new ArrayList<>();
        for(User user : users){
            UserListResDto userListResDto = new UserListResDto();
            userListResDto.setId(user.getId());
            userListResDto.setName(user.getName());
            userListResDto.setEmail(user.getEmail());
            userListResDtos.add(userListResDto);
        }
        return userListResDtos;
    }

    @Transactional
    public void updateProfile(long userId, @Valid ProfileUpdateReqDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ProfileUpdateReqDto.ProfileUpdateReqDtoBuilder profileUpdate = user.toProfileUpdateReqDto();
        // 닉네임 업데이트
        if (request.getNickname() != null) {
            // 중복 체크 (본인 제외)
            if (!request.getNickname().equals(user.getNickname())
                    && userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
            }
            profileUpdate.nickname(request.getNickname());
            log.info("사용자 {}의 닉네임 업데이트: {}", userId, request.getNickname());
        }

        // 전화번호 업데이트
        if (request.getPhone() != null) {
            profileUpdate.phone(request.getPhone());
            log.info("사용자 {}의 전화번호 업데이트", userId);
        }

        // 주소 업데이트
        if (request.getAddress() != null) {
            profileUpdate.address(request.getAddress());
            log.info("사용자 {}의 주소 업데이트", userId);
        }

        user.edit(profileUpdate.build());
    }

    public UserInfoResDto getUserInfo(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return new UserInfoResDto(user);
    }
}
