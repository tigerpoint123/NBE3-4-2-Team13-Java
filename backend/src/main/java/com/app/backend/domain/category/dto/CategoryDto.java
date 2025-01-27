package com.app.backend.domain.category.dto;

import com.app.backend.domain.category.entity.Category;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(
	Long id,

	@NotBlank
	String name
) {
	public static CategoryDto from(Category category) {
		return new CategoryDto(category.getId(), category.getName());
	}
}