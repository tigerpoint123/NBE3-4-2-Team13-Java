package com.app.backend.domain.member.util;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CommonUtil {

	public void setCookies(Cookie refreshTokenCookie, HttpServletResponse response) throws
		IOException {
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(1800); // 30분

		String cookieHeader = String.format("%s; SameSite=Strict", refreshTokenCookie.toString());
		response.setHeader("Set-Cookie", cookieHeader);
		response.addCookie(refreshTokenCookie);
		response.sendRedirect("http://localhost:3000/");
	}

	public void invalidateCookies(HttpServletResponse response) {
		// 기존 쿠키 무효화
		Cookie refreshTokenCookie = new Cookie("refreshToken", null);

		// 쿠키 설정
		refreshTokenCookie.setHttpOnly(true);
		refreshTokenCookie.setSecure(true);
		refreshTokenCookie.setPath("/");
		refreshTokenCookie.setMaxAge(0);  // 즉시 만료

		// SameSite 설정
		String refreshTokenCookieHeader = String.format("%s; SameSite=Lax", refreshTokenCookie.toString());
		response.addHeader("Set-Cookie", refreshTokenCookieHeader);
	}
}
