package com.app.backend.domain.member.controller;

import com.app.backend.domain.member.dto.kakao.TokenDto;
import com.app.backend.domain.member.service.KakaoAuthService;
import com.app.backend.domain.member.util.CommonUtil;
import com.app.backend.global.swagger.KakaoControllerInterface;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/kakao")
@Slf4j
public class KakaoController implements KakaoControllerInterface {
	private final KakaoAuthService kakaoAuthService;
	private final CommonUtil util;

	@GetMapping("/callback")
	public ResponseEntity<Map<String, String>> kakaoCallback(
		@RequestParam("code") String code, HttpServletResponse response
	) {
		TokenDto tokenDto = kakaoAuthService.kakaoLogin(code);
		// refreshToken만 쿠키에 저장
		Cookie refreshTokenCookie = new Cookie("refreshToken", tokenDto.refreshToken());
		// Access Token은 응답 본문에 포함
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("accessToken", tokenDto.accessToken());
		util.setCookies(refreshTokenCookie, response);

		return ResponseEntity.ok(responseBody);
	}
}
