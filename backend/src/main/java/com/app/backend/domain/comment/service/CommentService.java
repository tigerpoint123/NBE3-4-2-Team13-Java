package com.app.backend.domain.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.backend.domain.comment.dto.request.CommentCreateRequest;
import com.app.backend.domain.comment.dto.request.CommentReplyCreateRequest;
import com.app.backend.domain.comment.dto.response.CommentResponse;
import com.app.backend.domain.comment.entity.Comment;
import com.app.backend.domain.comment.exception.CommentErrorCode;
import com.app.backend.domain.comment.exception.CommentException;
import com.app.backend.domain.comment.repository.CommentRepository;
import com.app.backend.domain.member.entity.Member;
import com.app.backend.domain.member.repository.MemberRepository;
import com.app.backend.domain.post.entity.Post;
import com.app.backend.domain.post.exception.PostErrorCode;
import com.app.backend.domain.post.exception.PostException;
import com.app.backend.domain.post.repository.post.PostRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final MemberRepository memberRepository;

	//댓글 조회
	private Comment getCommentValidate(Long commentId){
		return commentRepository.findByIdAndDisabled(commentId, false)
			.orElseThrow(()-> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));
	}

	//게시물 조회
	private Post getPostValidate(Long postId){
		return postRepository.findByIdAndDisabled(postId, false)
			.orElseThrow(()-> new PostException(PostErrorCode.POST_NOT_FOUND));
	}

	//댓글 작성자만 수정과 삭제 가능
	private void validateAuthor(Comment comment, Long memberId) {
		if (!comment.getMember().getId().equals(memberId)) {
			throw new CommentException(CommentErrorCode.COMMENT_ACCESS_DENIED);
		}
	}

	//댓글 내용이 없으면 댓글 작성 실패
	private void validateCommentContent(String content) {
		if (content == null || content.trim().isEmpty()) {
			throw new CommentException(CommentErrorCode.COMMENT_INVALID_CONTENT);
		}
	}

	// 댓글 작성
	@Transactional
	public CommentResponse createComment(Long postId, Long memberId, CommentCreateRequest req) {

		validateCommentContent(req.getContent());

		Post post = getPostValidate(postId);

		//사용자 조회
		Member member = memberRepository.findByIdAndDisabled(memberId, false)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));


		Comment comment = Comment.builder()
			.content(req.getContent())
			.post(post)
			.member(member)
			.build();

		commentRepository.save(comment);

		return CommentResponse.from(comment);

	}

	// 댓글 삭제
	@Transactional
	public void deleteComment(Long commentId, Long memberId) {

		Comment comment = getCommentValidate(commentId);

		validateAuthor(comment, memberId);

		comment.delete();
	}

	// 댓글 수정
	@Transactional
	public CommentResponse updateComment(Long commentId, Long memberId, CommentCreateRequest req) {

		Comment comment = getCommentValidate(commentId);

		validateCommentContent(req.getContent());

		validateAuthor(comment, memberId);

		comment.update(req.getContent());

		return CommentResponse.from(comment);


	}

	// 댓글 조회
	public Page<CommentResponse> getComments(Long postId, Pageable pageable) {

		Post post = getPostValidate(postId);

		Page<Comment> comments = commentRepository.findByPostAndDisabled(post, false, pageable);

		return comments.map(CommentResponse::from);
	}


	@Transactional
	public CommentResponse createReply(Long postId, Long memberId, CommentReplyCreateRequest req) {

		Post post = getPostValidate(postId);

		// 부모 댓글 확인
		Comment parentComment = commentRepository.findByIdAndDisabled(req.getParentId(), false)
			.orElseThrow(() -> new CommentException(CommentErrorCode.PARENT_COMMENT_NOT_FOUND));

		Member member = memberRepository.findByIdAndDisabled(memberId, false)
			.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

		// 내용 검증
		validateCommentContent(req.getContent());


		Comment reply = Comment.builder()
			.content(req.getContent())
			.post(post)
			.member(member)
			.parent(parentComment)
			.build();

		Comment saveReply = commentRepository.save(reply);

		parentComment.addReply(saveReply);

		return CommentResponse.from(saveReply);
	}
}
