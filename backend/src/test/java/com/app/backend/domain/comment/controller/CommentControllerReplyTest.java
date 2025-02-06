package com.app.backend.domain.comment.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.comment.entity.Comment;
import com.app.backend.domain.comment.repository.CommentRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.entity.MemberDetails;
import com.app.backend.domain.member.repository.MemberRepository;
import com.app.backend.domain.post.entity.Post;
import com.app.backend.domain.post.entity.PostStatus;
import com.app.backend.domain.post.repository.post.PostRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentControllerReplyTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private CommentRepository commentRepository;

	private Post testPost;
	private Member testMember;
	private MemberDetails memberDetails;
	private Comment parentComment;

	@BeforeEach
	void setUp() {
		// 테스트용 멤버 생성
		testMember = Member.builder()
			.username("testUser")
			.password("password")
			.nickname("테스터")
			.role("USER")
			.disabled(false)
			.build();
		memberRepository.save(testMember);
		memberDetails = new MemberDetails(testMember);

		// 테스트용 게시물 생성
		testPost = Post.builder()
			.title("테스트 게시글")
			.content("테스트 내용")
			.postStatus(PostStatus.PUBLIC)
			.groupId(1L)
			.memberId(testMember.getId())
			.build();
		testPost = postRepository.save(testPost);

		// 테스트용 부모 댓글 생성
		parentComment = Comment.builder()
			.content("부모 댓글")
			.post(testPost)
			.member(testMember)
			.build();
		parentComment = commentRepository.save(parentComment);
	}

	@Test
	@DisplayName("대댓글 작성 성공")
	void createReply() throws Exception {

		ResultActions resultActions = mvc
			.perform(
				post("/api/v1/comment/" + parentComment.getId() + "/reply")
					.content("""
						{
							"content": "test"
						}
						""")

					.contentType(MediaType.APPLICATION_JSON)
					.with(user(memberDetails))
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("createReply"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.code").value("201"))
			.andExpect(jsonPath("$.data.content").value("test"))
			.andExpect(jsonPath("$.data.parentId").value(parentComment.getId()))
			.andExpect(jsonPath("$.data.postId").value(testPost.getId()))
			.andExpect(jsonPath("$.data.memberId").value(testMember.getId()))
			.andExpect(jsonPath("$.data.nickname").value(testMember.getNickname()))
			.andExpect(jsonPath("$.data.replies").isArray())
			.andExpect(jsonPath("$.data.replies").isEmpty());
	}

	@Test
	@DisplayName("대댓글 작성 실패 (내용 없음)")
	void createReply2() throws Exception {

		ResultActions resultActions = mvc
			.perform(
				post("/api/v1/comment/" + parentComment.getId() + "/reply")
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
			.andExpect(handler().methodName("createReply"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("CM004"))
			.andExpect(jsonPath("$.message").value("댓글 내용이 유효하지 않습니다"));
	}

	@Test
	@DisplayName("대댓글 작성 실패 (부모 댓글 없음)")
	void createReply3() throws Exception {

		ResultActions resultActions = mvc
			.perform(
				post("/api/v1/comment/0/reply")
					.content("""
						{
							"content": "test"
						}
						""")

					.contentType(MediaType.APPLICATION_JSON)
					.with(user(memberDetails))
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("createReply"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.isSuccess").value(false))
			.andExpect(jsonPath("$.code").value("CM001"))
			.andExpect(jsonPath("$.message").value("댓글을 찾을 수 없습니다"));
	}

}