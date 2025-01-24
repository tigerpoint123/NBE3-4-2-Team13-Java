package com.app.backend.member.dto.response;

import com.app.backend.member.entity.Member;

import java.time.LocalDateTime;

public record MemberResponse(
    Long id,
    String username,
    String nickname,
    String role,
    LocalDateTime createdAt
) {
    // Entity -> DTO 변환을 위한 정적 팩토리 메서드
    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getUsername(),
            member.getNickname(),
            member.getRole(),
            member.getCreatedAt()
        );
    }
} 