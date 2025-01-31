package com.app.backend.domain.member.controller;

import java.io.IOException;

import javax.security.sasl.AuthenticationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.domain.member.dto.kakao.TokenDto;
import com.app.backend.domain.member.service.KakaoAuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/kakao")
public class KakaoController {
	private final KakaoAuthService kakaoAuthService;

	@Operation(summary = "카카오 로그인 콜백", description = """
        스웨거에서 테스트하는 방법
        
        1. 브라우저에서 다음 URL을 호출하세요:
        https://kauth.kakao.com/oauth/authorize?client_id={본인의Client_Id}&redirect_uri={카카오API사이트에서 설정한 uri}&response_type=code
        
        접속하면 카카오 로그인같은 화면이 출력 (동의 화면을 계속 보고싶으면 code 뒤에 &prompt=login consent 추가)
        
        2. 로그인이 성공하면 본인이 작성한 uri에 code 값이 추가된 채로 이동됨 (URL 에서 확인 가능)
        
        3. 이 API의 code 파라미터에 복사한 값을 입력하세요
        """)
	@GetMapping("/callback")
	public ResponseEntity<?> kakaoCallback(
		@Parameter(description = "카카오 인증 코드")
		@RequestParam("code") String code, HttpServletResponse response
	) throws IOException {
		TokenDto tokenDto = kakaoAuthService.kakaoLogin(code);

		// 쿠키에 토큰 저장
		Cookie accessTokenCookie = new Cookie("accessToken", tokenDto.accessToken());
		Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.refreshToken());

		// 쿠키 설정
		accessTokenCookie.setHttpOnly(false);  // JavaScript에서 접근 방지
		accessTokenCookie.setSecure(true);    // HTTPS에서만 전송
		accessTokenCookie.setPath("/");
		accessTokenCookie.setDomain("localhost");
		accessTokenCookie.setMaxAge(3600);

		response.addCookie(accessTokenCookie);
		response.addCookie(refreshTokenCookie);
		response.sendRedirect("http://localhost:3000/");

		return ResponseEntity.status(HttpStatus.OK).body(null);
	}
}
