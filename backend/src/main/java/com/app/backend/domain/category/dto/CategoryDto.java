package com.app.backend.domain.category.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record CategoryDto(
	@NotBlank(message = "카테고리 이름은 필수입니다.")
	String name
) {
}