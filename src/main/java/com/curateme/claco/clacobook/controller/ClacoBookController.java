package com.curateme.claco.clacobook.controller;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curateme.claco.clacobook.domain.dto.request.UpdateClacoBookRequest;
import com.curateme.claco.clacobook.domain.dto.response.ClacoBookListResponse;
import com.curateme.claco.clacobook.domain.dto.response.ClacoBookResponse;
import com.curateme.claco.clacobook.service.ClacoBookServiceImpl;
import com.curateme.claco.global.annotation.TokenRefreshedCheck;
import com.curateme.claco.global.response.ApiResponse;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@TokenRefreshedCheck
@RestController
@RequestMapping("/api/claco-books")
@RequiredArgsConstructor
public class ClacoBookController {

	private final ClacoBookServiceImpl clacoBookService;

	@PostMapping
	@Operation(summary = "클라코북 생성", description = "클라코북 생성")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "사용자를 찾을 수 없음(잘못된 토큰 정보)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLB-010", description = "생성 한도 초과 = 5개 이상")
	})
	public ApiResponse<ClacoBookResponse> createClacoBook(@RequestBody UpdateClacoBookRequest request) {
		return ApiResponse.ok(clacoBookService.createClacoBook(request));
	}

	@GetMapping
	@Operation(summary = "접근 사용자의 클라코북 리스트 조회", description = "접근한 사용자의 클라코북 리스트 조회")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "사용자를 찾을 수 없음(잘못된 토큰 정보)")
	})
	public ApiResponse<ClacoBookListResponse> readClacoBookListWithOwner() {
		return ApiResponse.ok(new ClacoBookListResponse(clacoBookService.readClacoBooks()));
	}

	@PutMapping
	@Operation(summary = "클라코북 id 기반 수정", description = "클라코북의 이름과 색을 수정")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLB-001", description = "id에 해당하는 클라코북 찾을 수 없음")
	})
	public ApiResponse<ClacoBookResponse> updateClacoBook(@RequestBody UpdateClacoBookRequest request) {
		return ApiResponse.ok(clacoBookService.updateClacoBook(request));
	}

	@DeleteMapping("/claco-book/{bookId}")
	@Operation(summary = "클라코북 삭제", description = "클라코북 삭제, 소유주가 아니라면 삭제 불가")
	@Parameter(description = "삭제하고자 하는 클라코북 Id", example = "1")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLB-001", description = "id에 해당하는 클라코북 찾을 수 없음, 혹은 소유주 아님")
	})
	public ApiResponse<Void> deleteClacoBook(@PathVariable Long bookId) {
		clacoBookService.deleteClacoBook(bookId);
		return ApiResponse.ok();
	}

}
