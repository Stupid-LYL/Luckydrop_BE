package luckydrop.demo.member.service;

import lombok.RequiredArgsConstructor;
import luckydrop.demo.member.dto.request.MemberSaveReqDto;
import luckydrop.demo.member.entity.Member;
import luckydrop.demo.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    public Member create(MemberSaveReqDto memberSaveReqDto) {
        // 이미 가입되어 있는 이메일 검증
        if(memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
        Member newMember = Member.builder()
                .name(memberSaveReqDto.getName())
                .email(memberSaveReqDto.getEmail())
                .password(memberSaveReqDto.getPassword())
                .build();
        Member member = memberRepository.save(newMember);
        return member;
    }
}
