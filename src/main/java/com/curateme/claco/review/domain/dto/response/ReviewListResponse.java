package com.curateme.claco.review.domain.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.11.04
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.04		   이 건		   최초 생성
 * 2024.11.05		   이 건		   Swagger 적용
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewListResponse {

	// 총 페이지 수
	@Schema(description = "총 페이지 수", example = "3")
	private Integer totalPage;
	// 현재 페이지
	@Schema(description = "현재 페이지", example = "1")
	private Integer currentPage;
	// 요청 페이지 크기
	@Schema(description = "요청한 한 페이지의 크기", example = "5")
	private Integer size;
	// 리뷰 정보 리스트 (페이징 된)
	@Schema(description = "요청 리뷰 정보 리스트")
	private List<ReviewInfoResponse> reviewList;

}
