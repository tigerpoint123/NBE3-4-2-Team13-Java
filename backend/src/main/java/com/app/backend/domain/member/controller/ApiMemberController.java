package com.app.backend.domain.member.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.domain.member.dto.request.MemberJoinRequestDto;
import com.app.backend.domain.member.dto.request.MemberLoginRequestDto;
import com.app.backend.domain.member.dto.request.MemberModifyRequestDto;
import com.app.backend.domain.member.dto.response.MemberJoinResponseDto;
import com.app.backend.domain.member.dto.response.MemberLoginResponseDto;
import com.app.backend.domain.member.dto.response.MemberModifyResponseDto;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.entity.MemberDetails;
import com.app.backend.domain.member.jwt.JwtProvider;
import com.app.backend.domain.member.service.MemberService;
import com.app.backend.global.dto.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

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
		log.info("로그인 결과 : {}", response);
		return ApiResponse.of(
			true,
			"MEMBER_LOGIN_SUCCESS",
			"로그인이 성공적으로 완료되었습니다.",
			response
		);
	}

	// 회원정보 조회
	@Operation(summary = "회원 정보 조회", description = "JWT 토큰을 이용해 회원 정보를 조회합니다.")
	@Parameter(name = "Authorization", description = "Bearer JWT token", required = true, in = ParameterIn.HEADER)
	@GetMapping("/info")
	public ApiResponse<MemberDetails> getMemberInfo(
		@RequestHeader(value = "Authorization") String token
	) {
		log.info("토큰 : {}", token);
		
		return Optional.ofNullable(token)
			.map(t -> t.startsWith("Bearer ") ? t.substring(7) : t)
			.filter(jwtProvider::validateToken)
			.<ApiResponse<MemberDetails>>map(validToken -> {
				try {
					Member member = memberService.getCurrentMember(validToken); // 현재 사용자 조회
					return member != null
						? ApiResponse.of(true, "MEMBER_INFO_SUCCESS", "회원정보 조회에 성공했습니다", MemberDetails.of(member))
						: ApiResponse.of(false, "MEMBER_INFO_FAIL", "회원정보 조회에 실패했습니다");
				} catch (Exception e) {
					log.error("에러 내용 : ", e);
					return ApiResponse.of(false, "INVALID_TOKEN", "유효하지 않은 토큰입니다");
				}
			})
			.orElse(ApiResponse.of(false, "TOKEN_MISSING", "토큰이 필요합니다"));
	}

	@PatchMapping("/modify")
	public ApiResponse<MemberModifyResponseDto> modifyMemberInfo(
		@RequestHeader(value = "Authorization") String token,
		@RequestBody MemberModifyRequestDto request
	) {
		return Optional.ofNullable(token)
			.map(t -> t.startsWith("Bearer ") ? t.substring(7) : t)
			.filter(jwtProvider::validateToken)
			.<ApiResponse<MemberModifyResponseDto>>map(validToken -> {
				try {
					Member member = memberService.getCurrentMember(validToken); // 현재 사용자 조회
					if (member != null) {
						MemberModifyResponseDto response = memberService.modifyMember(member, request);
						return ApiResponse.of(true, "MEMBER_MODIFY_SUCCESS", "회원정보 수정에 성공했습니다", response);
					} else {
						return ApiResponse.of(false, "MEMBER_MODIFY_FAIL", "회원정보 수정에 실패했습니다");
					}
				} catch (Exception e) {
					log.error("에러 내용 : ", e);
					return ApiResponse.of(false, "INVALID_TOKEN", "유효하지 않은 토큰입니다");
				}
			})
			.orElse(ApiResponse.of(false, "TOKEN_MISSING", "토큰이 필요합니다"));
	}

}
