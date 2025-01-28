package com.app.backend.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.domain.member.dto.request.MemberJoinRequestDto;
import com.app.backend.domain.member.dto.request.MemberLoginRequestDto;
import com.app.backend.domain.member.dto.response.MemberJoinResponseDto;
import com.app.backend.domain.member.dto.response.MemberLoginResponseDto;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.entity.MemberDetails;
import com.app.backend.domain.member.jwt.JwtProvider;
import com.app.backend.domain.member.service.MemberService;
import com.app.backend.global.dto.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Slf4j
public class ApiMemberController {
	private final MemberService memberService;
	private final JwtProvider jwtProvider;

	//회원가입
	@PostMapping
	public ApiResponse<MemberJoinResponseDto> join(
		@RequestBody @Valid MemberJoinRequestDto request
	) {
		MemberJoinResponseDto response = memberService.createMember(request.username(), request.password(),
			request.nickname());
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
		log.info("login response : {}", response);
		return ApiResponse.of(
			true,
			"MEMBER_LOGIN_SUCCESS",
			"로그인이 성공적으로 완료되었습니다.",
			response
		);
	}

	// 회원정보 조회
	@GetMapping("/info")
	public ApiResponse<MemberDetails> getMemberInfo(
		@RequestHeader(value = "Authorization", required = false) String token
	) {
		log.info("토큰 : {}", token);
		// Bearer 토큰에서 실제 토큰 값만 추출
		// String actualToken = token.substring(7);
		Member member = memberService.getCurrentMember(token);
		if (member != null) {
			return ApiResponse.of(
				true,
				"MEMBER_INFO_SUCCESS",
				"회원정보 조회에 성공했습니다",
				MemberDetails.of(member)
			);
		} else {
			return ApiResponse.of(
				false,
				"MEMBER_INFO_FAIL",
				"회원정보 조회에 실패했습니다"
			);
		}
	}

}
