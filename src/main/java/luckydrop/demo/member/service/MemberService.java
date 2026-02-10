package luckydrop.demo.member.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import luckydrop.demo.common.auth.JwtTokenProvider;
import luckydrop.demo.member.dto.request.MemberLoginReqDto;
import luckydrop.demo.member.dto.request.MemberSaveReqDto;
import luckydrop.demo.member.dto.response.MemberListResDto;
import luckydrop.demo.member.dto.response.UserInfoResDto;
import luckydrop.demo.member.entity.Member;
import luckydrop.demo.member.entity.RefreshToken;
import luckydrop.demo.member.repository.MemberRepository;
import luckydrop.demo.member.repository.RefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.LoginException;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public Member create(MemberSaveReqDto memberSaveReqDto) {
        // 이미 가입되어 있는 이메일 검증
        if(memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        Member newMember = Member.builder()
                .name(memberSaveReqDto.getName())
                .nickname(memberSaveReqDto.getNickname())
                .phone(memberSaveReqDto.getPhone())
                .address(memberSaveReqDto.getAddress())
                .email(memberSaveReqDto.getEmail())
                .password(passwordEncoder.encode(memberSaveReqDto.getPassword()))
                .build();
        Member member = memberRepository.save(newMember);
        return member;
    }

    public Map<String, Object> login(MemberLoginReqDto memberLoginReqDto) {
        String email = memberLoginReqDto.getEmail();
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾지 못했습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(memberLoginReqDto.getPassword(), findMember.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(findMember.getEmail(), String.valueOf(findMember.getRole()));

        // 리프레시 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(findMember.getEmail(), String.valueOf(findMember.getRole()));

        // 리프레시 토큰의 만료 시간
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtTokenProvider.REFRESH_TOKEN_VALIDITY_SECONDS / 1000);

        // 토큰 빌더 엔티티 생성
        RefreshToken newRefreshToken = RefreshToken.builder()
                .member(findMember)
                .token(refreshToken)
                .expiredAt(refreshTokenExpireTime)
                .build();

        // 토큰 저장
        refreshTokenRepository.save(newRefreshToken);

        // 사용자 정보 생성
        UserInfoResDto userInfo = new UserInfoResDto(
                findMember.getId(),
                findMember.getNickname(),
                findMember.getEmail(),
                findMember.getRole()

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
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(email, role);
        // 새로운 리프레시 토큰 생성
        String newRefreshToken = jwtTokenProvider.createRefreshToken(email, role);
        // 만료시간(DB저장 용)
        LocalDateTime refreshTokenExpireTime = LocalDateTime.now().plusSeconds(JwtTokenProvider.REFRESH_TOKEN_VALIDITY_SECONDS / 1000);

        // 토큰 빌더 엔티티 생성
        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .member(findMember)
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
            log.info("리프레시 토큰을 DB에서 제거했습니다. user: {}", token.getMember().getEmail());
        });
    }

    public List<MemberListResDto> findAll(){
        List<Member> members = memberRepository.findAll();
        List<MemberListResDto> memberListResDtos = new ArrayList<>();
        for(Member member : members){
            MemberListResDto memberListResDto = new MemberListResDto();
            memberListResDto.setId(member.getId());
            memberListResDto.setName(member.getName());
            memberListResDto.setEmail(member.getEmail());
            memberListResDtos.add(memberListResDto);
        }
        return memberListResDtos;
    }
}
