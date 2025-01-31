package com.app.backend.domain.member.dto.kakao;

public record KakaoTokenResponse(
	String access_token,
	String token_type,
	String refresh_token,
	String expires_in,
	String scope
) {
}
