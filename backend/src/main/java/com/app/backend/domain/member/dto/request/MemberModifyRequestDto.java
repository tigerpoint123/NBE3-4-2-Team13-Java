package com.app.backend.domain.member.dto.request;

public record MemberModifyRequestDto(
	String nickname,
	String password
) {
}
