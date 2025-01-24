package com.app.backend.member.controller;

import com.app.backend.global.dto.response.ApiResponse;
import com.app.backend.member.dto.request.MemberCreateRequest;
import com.app.backend.member.dto.response.MemberResponse;
import com.app.backend.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ApiMemberController {
    private final MemberService memberService;

    @PostMapping("/members")
    public ApiResponse<MemberResponse> createMember(
            @RequestBody @Valid MemberCreateRequest request
            ) {
        MemberResponse response = memberService.createMember(request);
        return ApiResponse.of(
                true,
                "MEMBER_CREATE_SUCCESS",
                "회원가입이 성공적으로 완료되었습니다.",
                response
        );
    }

//    @GetMapping("/members/{id}")
//    public ApiResponse<MemberResponse> getMember(@PathVariable Long id) {
//        MemberResponse response = memberService.getMember(id);
//        return ApiResponse.of(
//                true,
//                "MEMBER_FIND_SUCCESS",
//                "회원 정보 조회가 성공적으로 완료되었습니다.",
//                response
//        );
//    }
}
