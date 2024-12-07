package com.curateme.claco.preference.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curateme.claco.global.annotation.TokenRefreshedCheck;
import com.curateme.claco.global.response.ApiResponse;
import com.curateme.claco.preference.domain.dto.request.PreferenceUpdateRequest;
import com.curateme.claco.preference.domain.dto.response.PreferenceInfoResponse;
import com.curateme.claco.preference.service.PreferenceService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.11.05
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.05   	   이 건        최초 생성
 */
@TokenRefreshedCheck
@RestController
@RequestMapping("/api/preferences")
@RequiredArgsConstructor
public class PreferenceController {

	private final PreferenceService preferenceService;

	/**
	 * 접근 유저의 취향 데이터 조회
	 * @return : 취향 데이터
	 */
	@Operation(summary = "취향 조회", description = "접근 유저 취향 조회 기능(나이, 성별, 선호 지역, 선호 공연 취향, 선호 공연 유형, 가격)")
	@GetMapping
	public ApiResponse<PreferenceInfoResponse> readPreference() {
		return ApiResponse.ok(preferenceService.readPreference());
	}

	/**
	 * 접근 유저의 취향 데이터 수정
	 * @param request : 취향 데이터 수정 정보 (성별, 나이, 가격, 지역, 타입)
	 * @return : 취향 데이터 (수정 후)
	 */
	@Operation(summary = "취향 수정", description = "접근 유저 취향 수정 기능")
	@PutMapping
	public ApiResponse<PreferenceInfoResponse> updatePreference(@RequestBody PreferenceUpdateRequest request) {
		return ApiResponse.ok(preferenceService.updatePreference(request));
	}

}
