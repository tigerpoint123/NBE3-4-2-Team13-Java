package com.app.backend.domain.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(
	@NotBlank
	String name
) {
}