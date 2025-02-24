package com.app.backend.domain.comment.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.comment.entity.Comment;
import com.app.backend.domain.comment.repository.CommentLikeRepository;
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
class CommentLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    private Member testMember;
    private Post testPost;
    private Comment testComment;
    private MemberDetails memberDetails;

    @BeforeEach
    void setUp() {
        // 테스트 멤버 생성
        testMember = Member.builder()
            .username("testUser")
            .password("password")
            .nickname("테스터")
            .role("ROLE_USER")
            .disabled(false)
            .build();
        memberRepository.save(testMember);

        // 테스트 게시물 생성
        testPost = Post.builder()
            .title("Test Post")
            .content("Test Content")
            .nickName("테스터")
            .memberId(testMember.getId())
            .groupId(1L)
            .postStatus(PostStatus.PUBLIC)
            .build();
        postRepository.save(testPost);

        // 테스트 댓글 생성
        testComment = Comment.builder()
            .content("Test Comment")
            .member(testMember)
            .post(testPost)
            .build();
        commentRepository.save(testComment);

        // MemberDetails 생성
        memberDetails = new MemberDetails(testMember);
    }

    @Test
    @DisplayName("댓글 좋아요 추가 성공")
    void createCommentLike() throws Exception {
        // when
        mockMvc.perform(post("/api/v1/comment/{id}/like", testComment.getId())
                .with(user(memberDetails)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isSuccess").value(true))
            .andExpect(jsonPath("$.message").value("댓글 좋아요가 추가되었습니다."));

        // then
		long likeCount = commentLikeRepository.countByCommentIdAndDisabled(testComment.getId(), false);
        assertThat(likeCount).isEqualTo(1);
    }


    @Test
    @DisplayName("삭제된 댓글에 좋아요 시도 시 실패")
    void createCommentLike2() throws Exception {
        // given
        testComment.delete();
        commentRepository.save(testComment);

        // when & then
        mockMvc.perform(post("/api/v1/comment/{id}/like", testComment.getId())
                .with(user(memberDetails)))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.isSuccess").value(false))
            .andExpect(jsonPath("$.code").value("CM001"));
    }


	@Test
	@DisplayName("여러 사용자의 동시 좋아요 정합성 테스트")
	void getCommentLikeCount2() throws Exception {
		int numberOfUsers = 5;
		List<MemberDetails> memberDetailsList = new ArrayList<>();


		for (int i = 0; i < numberOfUsers; i++) {
			Member user = Member.builder()
				.username("testUser" + i)
				.password("password")
				.nickname("테스터" + i)
				.role("ROLE_USER")
				.disabled(false)
				.build();
			memberDetailsList.add(new MemberDetails(memberRepository.save(user)));
		}

		// 여러 사용자가 동시에 좋아요 요청
		for (MemberDetails userDetails : memberDetailsList) {
			mockMvc.perform(post("/api/v1/comment/{id}/like", testComment.getId())
					.with(user(userDetails)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isSuccess").value(true));
		}


		long LikeCount = commentLikeRepository.countByCommentIdAndDisabled(testComment.getId(), false);
		assertThat(LikeCount).isEqualTo(numberOfUsers);
	}

	@Test
	@DisplayName("동일 사용자의 연속 좋아요 정합성")
	void getCommentLikeCount3() throws Exception {

		int toggleCount = 2;

		for (int i = 0; i < toggleCount; i++) {
			mockMvc.perform(post("/api/v1/comment/{id}/like", testComment.getId())
					.with(user(memberDetails)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.isSuccess").value(true));
		}


		long finalLikeCount = commentLikeRepository.countByCommentIdAndDisabled(testComment.getId(), false);
		assertThat(finalLikeCount).isEqualTo(0);

		boolean hasLike = commentLikeRepository.findByCommentIdAndMemberIdAndDisabled(
			testComment.getId(), testMember.getId(), false).isPresent();
		assertThat(hasLike).isFalse();
	}


	@Test
	@DisplayName("댓글 좋아요 수와 기록 수의 정합성 테스트")
	void getCommentLikeCount4() throws Exception {
		int expectedLikeCount = 100;

		for (int i = 0; i < expectedLikeCount; i++) {
			Member user = Member.builder()
				.username("testUser" + i)
				.password("password")
				.nickname("테스터" + i)
				.role("ROLE_USER")
				.build();

			mockMvc.perform(post("/api/v1/comment/{id}/like", testComment.getId())
					.with(user(new MemberDetails(memberRepository.save(user)))))
				.andExpect(status().isOk());
		}

		long LikeCount = commentLikeRepository.countByCommentIdAndDisabled(testComment.getId(), false);
		long actualCount = commentLikeRepository.findAll().stream()
			.filter(like -> like.getComment().getId().equals(testComment.getId()) && !like.getDisabled())
			.count();

		// then
		assertThat(LikeCount).isEqualTo(expectedLikeCount);
		assertThat(actualCount).isEqualTo(expectedLikeCount);
		assertThat(LikeCount).isEqualTo(actualCount);
	}


	

} 