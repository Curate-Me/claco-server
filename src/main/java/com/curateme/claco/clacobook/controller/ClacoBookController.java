package com.curateme.claco.clacobook.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curateme.claco.clacobook.domain.dto.request.UpdateClacoBookRequest;
import com.curateme.claco.clacobook.domain.dto.response.ClacoBookResponse;
import com.curateme.claco.clacobook.service.ClacoBookService;
import com.curateme.claco.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/claco-books")
@RequiredArgsConstructor
public class ClacoBookController {

	private final ClacoBookService clacoBookService;

	@PostMapping("/claco-book")
	public ApiResponse<Void> createClacoBook() {
		clacoBookService.createClacoBook();
		return ApiResponse.ok();
	}

	@GetMapping
	public ApiResponse<List<ClacoBookResponse>> readClacoBookListWithOwner() {
		return ApiResponse.ok(clacoBookService.readClacoBooks());
	}

	@PutMapping("/claco-book")
	public ApiResponse<ClacoBookResponse> updateClacoBook(@Validated @RequestBody UpdateClacoBookRequest request) {
		return ApiResponse.ok(clacoBookService.updateClacoBook(request));
	}

	@DeleteMapping("/claco-book/{bookId}")
	public ApiResponse<Void> deleteClacoBook(@PathVariable Long bookId) {
		clacoBookService.deleteClacoBook(bookId);
		return ApiResponse.ok();
	}

}
