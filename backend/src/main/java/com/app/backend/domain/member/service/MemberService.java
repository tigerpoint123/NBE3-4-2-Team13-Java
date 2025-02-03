package com.app.backend.domain.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.member.dto.request.MemberLoginRequestDto;
import com.app.backend.domain.member.dto.request.MemberModifyRequestDto;
import com.app.backend.domain.member.dto.response.MemberJoinResponseDto;
import com.app.backend.domain.member.dto.response.MemberLoginResponseDto;
import com.app.backend.domain.member.dto.response.MemberModifyResponseDto;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.jwt.JwtProvider;
import com.app.backend.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final boolean disabled = false;

    @Transactional
    public MemberJoinResponseDto createMember(String username, String password, String nickname) {
        memberRepository.findByUsernameAndDisabled(username, disabled)
                .ifPresent(a -> {
                    throw new ServiceException("409-1", "이미 존재하는 username 입니다.");
                });
        memberRepository.findByNicknameAndDisabled(nickname, disabled)
                .ifPresent(a->{
                    throw new ServiceException("409-2", "이미 존재하는 닉네임입니다");
                });

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .role("ADMIN")
                .disabled(false)
                .build();

        Member savedMember = memberRepository.save(member);

        // Entity -> Response DTO 변환
        return MemberJoinResponseDto.from(savedMember);
    }

    public MemberLoginResponseDto login(MemberLoginRequestDto request) {
        // 사용자 찾기
        Member member = memberRepository.findByUsernameAndDisabled(request.username(), disabled)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자입니다."));

        // 비밀번호 확인
        if(!passwordEncoder.matches(request.password(), member.getPassword()))
            throw new IllegalArgumentException("잘못된 비밀번호입니다");

        // 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(member);
        String refreshToken = jwtProvider.generateRefreshToken();

        // refresh token 저장
        member.updateRefreshToken(refreshToken);
        memberRepository.save(member);

        // 응답
        return MemberLoginResponseDto.of(member, accessToken, refreshToken);
    }

    public Member getCurrentMember(String accessToken) {
        return Optional.ofNullable(accessToken)
            .map(t -> t.startsWith("Bearer ") ? t.substring(7) : t)
            .filter(jwtProvider::validateToken)
            .map(validateToken -> {
                Long memberId = jwtProvider.getMemberId(validateToken);
                return this.memberRepository.findByIdAndDisabled(memberId, false)
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자입니다"));
            })
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않는 토큰입니다"));  // 토큰 검증
    }


    @Transactional
    public MemberModifyResponseDto modifyMember(Member member, MemberModifyRequestDto request) {
        Member modifiedMember = Member.builder()
            .id(member.getId())
            .username(member.getUsername())
            .password(request.password() != null ?
                passwordEncoder.encode(request.password()) : member.getPassword())
            .nickname(member.getNickname())
            .role(member.getRole())
            .disabled(member.isDisabled())
            .build();

        Member savedMember = Optional.of(
            memberRepository.save(modifiedMember)
        ).orElseThrow(() -> new ServiceException("400", "회원정보 수정에 실패했습니다"));

        return MemberModifyResponseDto.of(savedMember);
    }

    public Optional<List<Member>> findAllMembers(String token) {
        return Optional.ofNullable(token)
            .map(t -> t.startsWith("Bearer ") ? t.substring(7) : t)
            .filter(jwtProvider::validateToken)
            .map(validateToken -> memberRepository.findAll());
    }
}
