package com.curateme.claco.review.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curateme.claco.global.annotation.TokenRefreshedCheck;
import com.curateme.claco.global.response.ApiResponse;
import com.curateme.claco.review.domain.dto.response.CategoryListResponse;
import com.curateme.claco.review.domain.vo.PlaceCategoryVO;
import com.curateme.claco.review.service.PlaceCategoryService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@TokenRefreshedCheck
@RestController
@RequestMapping("/api/place-categories")
@RequiredArgsConstructor
public class PlaceCategoryController {

	private final PlaceCategoryService placeCategoryService;

	@GetMapping
	@Operation(summary = "장소평 카테고리 조회", description = "장소평의 카테고리 조회")
	public ApiResponse<CategoryListResponse<List<PlaceCategoryVO>>> readPlaceCategoryList() {
		return ApiResponse.ok(new CategoryListResponse<>(placeCategoryService.readPlaceCategoryList()));
	}

}
