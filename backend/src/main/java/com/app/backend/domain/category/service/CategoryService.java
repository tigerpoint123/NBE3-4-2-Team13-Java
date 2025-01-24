package com.app.backend.domain.category.service;

import org.springframework.stereotype.Service;

import com.app.backend.domain.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

}
