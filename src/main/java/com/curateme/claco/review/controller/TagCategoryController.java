package com.curateme.claco.review.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curateme.claco.global.annotation.TokenRefreshedCheck;
import com.curateme.claco.global.response.ApiResponse;
import com.curateme.claco.review.domain.dto.response.CategoryListResponse;
import com.curateme.claco.review.domain.vo.TagCategoryVO;
import com.curateme.claco.review.service.TagCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@TokenRefreshedCheck
@RestController
@RequestMapping("/api/tag-categories")
@RequiredArgsConstructor
public class TagCategoryController {

	private final TagCategoryService tagCategoryService;

	@GetMapping
	@Operation(summary = "공연 성격의 카테고리 조회", description = "공연 성격의 카테고리 조회")
	public ApiResponse<CategoryListResponse<List<TagCategoryVO>>> readTagCategoryList() {
		return ApiResponse.ok(new CategoryListResponse<>(tagCategoryService.readTagCategoryList()));
	}

}
