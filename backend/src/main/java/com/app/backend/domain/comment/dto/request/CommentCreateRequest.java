package com.app.backend.domain.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentCreateRequest {

	@NotBlank(message = "내용은 필수입니다.")
	private String content;
}


