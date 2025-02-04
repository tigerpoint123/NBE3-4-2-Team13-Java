package com.app.backend.domain.comment.dto.response;

import java.time.LocalDateTime;

import com.app.backend.domain.comment.entity.Comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {
	private Long id;
	private String content;
	private Long postId;
	private Long memberId;
	private String nickname;
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	// Entity -> DTO 변환
	public static CommentResponse from(Comment comment) {
		return CommentResponse.builder()
			.id(comment.getId())
			.content(comment.getContent())
			.postId(comment.getPost().getId())
			.memberId(comment.getMember().getId())
			.nickname(comment.getMember().getNickname())
			.createdAt(comment.getCreatedAt())
			.modifiedAt(comment.getModifiedAt())
			.build();
	}

}
