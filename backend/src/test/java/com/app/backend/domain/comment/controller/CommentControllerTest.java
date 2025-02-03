package com.app.backend.domain.comment.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.comment.repository.CommentRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.entity.MemberDetails;
import com.app.backend.domain.member.repository.MemberRepository;
import com.app.backend.domain.post.entity.Post;
import com.app.backend.domain.post.entity.PostStatus;
import com.app.backend.domain.post.repository.post.PostRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class CommentControllerTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;

	private Long testPostId;
	private Member testMember;


	@BeforeEach
	void setUp() {
		// 테스트용
		testMember = Member.builder()
			.username("testUser")
			.password("password")
			.nickname("테스터")
			.role("USER")
			.disabled(false)
			.build();
		memberRepository.save(testMember);


		Post post = Post.builder()
			.title("테스트 게시글")
			.content("테스트 내용")
			.postStatus(PostStatus.PUBLIC)
			.groupId(1L)
			.memberId(testMember.getId())
			.build();

		Post savedPost = postRepository.save(post);
		testPostId = savedPost.getId();
	}

	@Test
	@DisplayName("댓글 작성")
	void createComment() throws Exception {
		MemberDetails memberDetails = new MemberDetails(testMember);

		ResultActions resultActions = mvc
			.perform(
				post("/api/v1/comment/" + testPostId)
					.content("""
						{
							"content": "test"
						}
						""")
					.contentType(MediaType.APPLICATION_JSON)
					.with(user(memberDetails)) // 로그인된 사용자 정보

			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("createComment"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.message").exists());
	}

	@Test
	@DisplayName("댓글 작성 실패 (내용 공백)")
	void createComment2() throws Exception {

		MemberDetails memberDetails = new MemberDetails(testMember);

		ResultActions resultActions = mvc
			.perform(
				post("/api/v1/comment/" + testPostId)
					.content("""
						{
							"content": ""
						}
						""")
					.contentType(MediaType.APPLICATION_JSON)
					.with(user(memberDetails))
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("createComment"))
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("CM004"))
			.andExpect(jsonPath("$.message").value("댓글 내용이 유효하지 않습니다"));
	}


}
