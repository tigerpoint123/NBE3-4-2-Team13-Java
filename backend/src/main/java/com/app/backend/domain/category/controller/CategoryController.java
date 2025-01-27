package com.app.backend.domain.category.controller;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.domain.category.dto.CategoryDto;
import com.app.backend.domain.category.entity.Category;
import com.app.backend.domain.category.service.CategoryService;
import com.app.backend.domain.post.entity.Post;
import com.app.backend.global.dto.response.ApiResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

	private final CategoryService categoryService;

	public record CreateCategoryReqBody(
		@NotBlank
		@Length(max = 10)
		String name
	) {
	}

	@PostMapping
	public ApiResponse<Void> createCategory(
		@RequestBody CreateCategoryReqBody request
	) {
		Category category = categoryService.create(request.name());

		return ApiResponse.of(
			true,
			"201",
			"%s 카테고리가 생성되었습니다.".formatted(category.getName())
		);
	}

}
