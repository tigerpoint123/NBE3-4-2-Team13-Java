package com.app.backend.domain.comment.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.backend.domain.comment.dto.request.CommentCreateRequest;
import com.app.backend.domain.comment.dto.response.CommentResponse;
import com.app.backend.domain.comment.service.CommentService;
import com.app.backend.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	// 게시물에 대한 댓글 작성
	@PostMapping("/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ApiResponse<CommentResponse> createComment(@PathVariable(name = "id") long id,
		@RequestBody CommentCreateRequest req){


		CommentResponse response = commentService.createComment(id, req);

		return ApiResponse.of(
			true,
			"201",
			"%d번 댓글이 작성되었습니다.".formatted(response.getId()),
			response
		);
	}
}
