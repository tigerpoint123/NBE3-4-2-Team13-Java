package com.app.backend.domain.member;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.app.backend.domain.member.controller.KakaoController;
import com.app.backend.domain.member.dto.request.MemberJoinRequestDto;
import com.app.backend.domain.member.dto.request.MemberLoginRequestDto;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.jwt.JwtProvider;
import com.app.backend.domain.member.repository.MemberRepository;
import com.app.backend.domain.member.service.KakaoAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest           // 전체 애플리케이션 컨텍스트 로드
@AutoConfigureMockMvc     // MockMvc 자동 구성
@Transactional           // 테스트 후 롤백
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private ObjectMapper objectMapper;  // JSON 변환을 위한 객체
	@Mock
	private KakaoAuthService kakaoAuthService;
	@Autowired
	private KakaoController kakaoController;
	@Autowired
	private JwtProvider jwtProvider;

	// 기존 회원 셋업
	private static final String setupUsername = "testID";
	private static final String setupPassword = "testPW";
	private static final String setupNickname = "김호남";

	private static final String newUsername = "username";
	private static final String newPassword = "password";
	private static final String newNickname = "김영남";

	@BeforeEach
	void 셋업() throws Exception {
		//given
		MemberJoinRequestDto request = new MemberJoinRequestDto(setupUsername, setupPassword, setupNickname);

		//when
		mvc.perform(post("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())  // HTTP 상태코드 검증
			.andDo(print());  // 결과 출력
	}

	@Test
	@DisplayName("회원가입")
	void 회원가입() throws Exception {
		//given
		MemberJoinRequestDto request = new MemberJoinRequestDto(newUsername, newPassword, newNickname);

		//when
		mvc.perform(post("/api/v1/members")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())  // HTTP 상태코드 검증
			.andDo(print());  // 결과 출력

		//then
		// DB에 실제로 저장되었는지 확인
		Member savedMember = memberRepository.findByUsernameAndDisabled(newUsername, false)
			.orElseThrow(() -> new RuntimeException("회원이 저장되지 않았습니다."));

		assertAll(
			() -> assertEquals(request.username(), savedMember.getUsername()),
			() -> assertEquals(request.nickname(), savedMember.getNickname()),
			// 비밀번호는 암호화되어 저장되므로 직접 비교는 하지 않음
			() -> assertNotNull(savedMember.getPassword())
		);
	}

	@Test
	@DisplayName("관리자 로그인")
	void 관리자로그인() throws Exception {
		//given
		MemberLoginRequestDto request = new MemberLoginRequestDto(setupUsername, setupPassword);

		//when
		mvc.perform(post("/api/v1/members/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())  // HTTP 상태코드 검증
			.andDo(print());  // 결과 출력

		// then
		Member savedMember = memberRepository.findByUsernameAndDisabled(setupUsername, false)
			.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자입니다"));

		assertAll(
			() -> assertEquals(request.username(), savedMember.getUsername()),
			() -> assertNotNull(savedMember.getPassword())
		);
	}

	@Test
	@DisplayName("카카오 로그인")
	void 카카오로그인() throws Exception {
		// // given
		// String accessToken = jwtProvider.generateAccessToken(
		// 	Member.builder()
		// 		.username("1")
		// 		.nickname("김호남")
		// 		.role("ROLE_USER")
		// 		.build()
		// );
		// String refreshToken = jwtProvider.generateRefreshToken();
		// KakaoUserInfo kakaoUserInfo = new KakaoUserInfo("1", "김호남");
		// when(kakaoAuthService.getKakaoUserInfo(anyString()))
		// 	.thenReturn(kakaoUserInfo);
		//
		// //when
		// mvc.perform(post("/api/v1/members/kakao/callback")
		// 		.contentType(MediaType.APPLICATION_JSON)
		// 		.content(objectMapper.writeValueAsString(
		// 			new KakaoLoginRequestDto("1", "김호남", "KAKAO"))))
		// 	.andExpect(status().isOk())
		// 	.andExpect(jsonPath("$.accessToken").exists())
		// 	.andExpect(jsonPath("$.refreshToken").exists())
		// 	.andDo(print());  // 결과 출력
		//
		// // then
		// Member savedMember = memberRepository.findByUsername(setupNickname)
		// 	.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자입니다"));
		//
		// assertAll(
		// 	() -> assertEquals(refreshToken, savedMember.getRefreshToken())
		// );
	}
}
