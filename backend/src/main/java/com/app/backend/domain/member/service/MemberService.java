package com.app.backend.domain.member.service;

import com.app.backend.domain.member.dto.request.MemberCreateRequest;
import com.app.backend.domain.member.dto.response.MemberResponse;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.repository.MemberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponse createMember(@Valid MemberCreateRequest request) {
        Member member = Member.builder()
                .username("test id")
                .password(passwordEncoder.encode("1234"))
                .nickname("김호남")
                .role("USER")
                .disabled(false)
                .build();

        // Repository를 통한 저장
        Member savedMember = memberRepository.save(member);

        // Entity -> Response DTO 변환
        return MemberResponse.from(savedMember);
    }
}
