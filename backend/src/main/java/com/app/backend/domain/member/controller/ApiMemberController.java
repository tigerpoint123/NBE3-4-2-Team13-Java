package com.app.backend.domain.member.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

	@GetMapping
	public ApiResponse<MemberDetails> getMyInfo(
		@RequestHeader("Authorization") String bearerToken
	) {
		try {
			// "Bearer " 제거하고 실제 토큰만 추출
			String token = bearerToken.substring(7);

			log.info("전달받은 토큰: {}", token);

			// 토큰 유효성 검증
			if (!jwtProvider.validateToken(token)) {
				log.error("유효하지 않은 토큰");
				return ApiResponse.of(
					false,
					"INVALID_TOKEN",
					"유효하지 않은 토큰입니다."
				);
			}

			// 토큰에서 Authentication 객체 가져오기
			Authentication authentication = jwtProvider.getAuthentication(token);
			MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

			log.info("조회된 사용자: id={}, username={}",
				memberDetails.getId(),
				memberDetails.getUsername());

			return ApiResponse.of(
				true,
				"MEMBER_INFO_SUCCESS",
				"사용자 정보 조회에 성공했습니다.",
				memberDetails
			);

		} catch (Exception e) {
			log.error("사용자 정보 조회 실패: {}", e.getMessage());
			return ApiResponse.of(
				false,
				"INVALID_TOKEN",
				"유효하지 않은 토큰입니다."
			);
		}

	}

}
