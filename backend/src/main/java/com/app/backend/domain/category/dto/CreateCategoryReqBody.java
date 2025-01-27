package com.app.backend.domain.category.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryReqBody(
	@NotBlank
	@Length(max = 10)
	String name
) {
}