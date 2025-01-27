package com.app.backend.domain.category.controller;


import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.backend.domain.category.dto.CategoryDto;
import com.app.backend.domain.category.dto.CategoryPageDto;
import com.app.backend.domain.category.dto.CreateCategoryReqBody;
import com.app.backend.domain.category.entity.Category;
import com.app.backend.domain.category.service.CategoryService;
import com.app.backend.global.dto.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	public ApiResponse<CategoryDto> createCategory(
		@RequestBody CreateCategoryReqBody request
	) {
		Category category = categoryService.create(request.name());

		CategoryDto categoryDto = CategoryDto.from(category);

		return ApiResponse.of(
			true,
			"201",
			"%s 카테고리가 생성되었습니다.".formatted(category.getName()),
			categoryDto
		);
	}

	@GetMapping
	public ApiResponse<CategoryPageDto> getCategories(
		@PageableDefault(size = 10) Pageable pageable
	) {
		CategoryPageDto categoryPageDto = categoryService.getCategories(pageable);

		return ApiResponse.of(
			true,
			"200",
			"카테고리 목록 조회",
			categoryPageDto
		);
	}

}
