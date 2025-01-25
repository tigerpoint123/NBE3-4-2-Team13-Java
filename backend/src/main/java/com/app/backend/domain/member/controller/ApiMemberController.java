package com.app.backend.domain.member.controller;

import com.app.backend.domain.member.dto.request.MemberLoginRequestDto;
import com.app.backend.domain.member.dto.response.MemberLoginResponseDto;
import com.app.backend.global.dto.response.ApiResponse;
import com.app.backend.domain.member.dto.request.MemberJoinRequestDto;
import com.app.backend.domain.member.dto.response.MemberJoinResponseDto;
import com.app.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiMemberController {
    private final MemberService memberService;

    //회원가입
    @PostMapping
    public ApiResponse<MemberJoinResponseDto> join(
            @RequestBody @Valid MemberJoinRequestDto request
            ) {
        MemberJoinResponseDto response = memberService.createMember(request.username(), request.password(), request.nickname());
        return ApiResponse.of(
                true,
                "MEMBER_CREATE_SUCCESS",
                "회원가입이 성공적으로 완료되었습니다.",
                response
        );
    }

    // 로그인
    @PostMapping("/login")
    public ApiResponse<MemberLoginResponseDto> login(
            @RequestBody MemberLoginRequestDto request) {
        MemberLoginResponseDto response = memberService.login(request);
        return ApiResponse.of(
                true,
                "MEMBER_LOGIN_SUCCESS",
                "로그인이 성공적으로 완료되었습니다.",
                response
        );
    }
}
