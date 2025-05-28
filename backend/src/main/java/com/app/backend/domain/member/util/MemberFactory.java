package com.app.backend.domain.member.util;

import com.app.backend.domain.member.entity.Member;

public class MemberFactory {

    public static Member createUser(String username, String password, String nickname) {
        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .role("ROLE_USER")
                .disabled(false)
                .build();
    }

    public static Member createAdmin(String username, String password, String nickname) {
        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .role("ROLE_ADMIN")
                .disabled(false)
                .build();
    }

    public static Member modifyMember(Member member, String password) {
        return Member.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(password)
                .nickname(member.getNickname())
                .role(member.getRole())
                .disabled(member.isDisabled())
                .build();
    }

    public static Member deleteMember(Member member) {
        return Member.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .provider(member.getProvider())
                .oauthProviderId(member.getOauthProviderId())
                .role(member.getRole())
                .disabled(true)
                .build();
    }

    public static Member createKakaoUser(String oauthId, String nickname) {
        return Member.builder()
                .username(oauthId)
                .nickname(nickname)
                .provider(Member.Provider.KAKAO)
                .oauthProviderId(oauthId)
                .role("ROLE_USER")
                .disabled(false)
                .build();
    }
}
