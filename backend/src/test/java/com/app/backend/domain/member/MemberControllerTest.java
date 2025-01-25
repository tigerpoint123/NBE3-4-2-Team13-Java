package com.app.backend.domain.member;

import com.app.backend.domain.member.controller.ApiMemberController;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.service.MemberService;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest           // 전체 애플리케이션 컨텍스트 로드
@AutoConfigureMockMvc     // MockMvc 자동 구성
@Transactional           // 테스트 후 롤백
public class MemberControllerTest {
//    @Autowired
//    public MemberService memberService;
//    @Autowired
//    private MockMvc mvc;
//
//    @Test
//    @DisplayName("회원가입")
//    void 회원가입() throws Exception {
//        ResultActions resultActions = mvc
//                .perform(
//                        post("/api/v1/members")
//                                .content("""
//                                        {
//                                            "username": "user",
//                                            "password": "1234",
//                                            "nickname": "김호남"
//                                        """.stripIndent())
//                                .contentType(
//                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
//                                )
//                ).andDo(print());
//
//        Member member = memberService.findByUsername("user").get();
//
//        resultActions
//                .andExpect(handler().handlerType(ApiMemberController.class))
//                .andExpect(handler().methodName("join"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.resultCode").value("201-1"))
//                .andExpect(jsonPath("$.msg").value("%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(member.getNickname())))
//                .andExpect(jsonPath("$.data").exists())
//                .andExpect(jsonPath("$.data.id").value(member.getId()))
//                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(member.getCreatedAt().toString().substring(0, 20))))
//                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.startsWith(member.getModifiedAt().toString().substring(0, 20))))
//                .andExpect(jsonPath("$.data.nickname").value(member.getNickname()));
//    }

//    @Test
//    @DisplayName("로그인")
//    void 로그인() {
//
//    }
}
