package com.app.backend.domain.member.util;

import com.app.backend.domain.member.entity.Member;

public class MemberFactory {

    public static Member createMember(String username, String password, String nickname) {
        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .role("USER")
                .build();
    }
}
