package com.app.backend.domain.member.service;

import com.app.backend.domain.member.dto.kakao.KakaoUserInfo;
import com.app.backend.domain.member.dto.kakao.TokenDto;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.exception.MemberErrorCode;
import com.app.backend.domain.member.exception.MemberException;
import com.app.backend.domain.member.jwt.JwtProvider;
import com.app.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {
    private final RestTemplate restTemplate;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Transactional
    public TokenDto kakaoLogin(String code) {
        try {
            // 1. 인가코드로 액세스 토큰 요청
            String kakaoAccessToken = getKakaoAccessToken(code);

            // 2. 액세스 토큰으로 카카오 API 호출
            KakaoUserInfo userInfo = getKakaoUserInfo(kakaoAccessToken);

            // 3. 회원가입 & 로그인 처리
            Member member = saveOrUpdate(userInfo);

            // 4. JWT 토큰 발급
            String accessToken = jwtProvider.generateAccessToken(member);
            String refreshToken = jwtProvider.generateRefreshToken();

            memberRepository.save(member);

            return new TokenDto(userInfo.id(), accessToken, refreshToken, "USER");
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류: {}", e.getMessage());
            if (e.getMessage().contains("authorization code not found")) {
                log.warn("이미 사용된 인증 코드입니다.");
            }
            throw e;
        }
    }

    private String getKakaoAccessToken(String code) {
        try {
            String tokenUri = "https://kauth.kakao.com/oauth/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("redirect_uri", redirectUri);
            params.add("code", code);
            params.add("client_secret", clientSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUri,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
                return (String) response.getBody().get("access_token");

            throw new MemberException(MemberErrorCode.MEMBER_FAILED_TO_KAKAO_TOKEN);
        } catch (Exception e) {
            log.error("카카오 토큰 요청 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("카카오 토큰 요청 실패", e);
        }
    }

    // 필수 동의항목 설정 필요
    public KakaoUserInfo getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me", // 카카오 사용자 정보 조회 api
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            Map<String, Object> body = response.getBody();
            Map<String, Object> properties = (Map<String, Object>) body.get("properties");

            return new KakaoUserInfo(
                    String.valueOf(body.get("id")),
                    (String) properties.get("nickname")
            );
        } catch (Exception e) {
            log.error("카카오 API 호출 실패: {}", e.getMessage());
            throw new MemberException(MemberErrorCode.MEMBER_FAILED_TO_KAKAO_AUTH);
        }
    }

    private Member saveOrUpdate(KakaoUserInfo userInfo) {
        return memberRepository.findByOauthProviderId(userInfo.id())
                .orElseGet(() -> memberRepository.save(
                        Member.builder()
                                .username(userInfo.id())
                                .password("")
                                .nickname(userInfo.nickname())
                                .provider(Member.Provider.KAKAO)
                                .oauthProviderId(userInfo.id())
                                .role("ROLE_USER")
                                .disabled(false)
                                .build()));
    }
}
