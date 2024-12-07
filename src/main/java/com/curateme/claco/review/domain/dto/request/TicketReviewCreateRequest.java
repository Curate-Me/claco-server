package com.curateme.claco.review.domain.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.curateme.claco.review.domain.vo.PlaceCategoryVO;
import com.curateme.claco.review.domain.vo.TagCategoryVO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketReviewCreateRequest {

	// 공연 id
	@NotNull
	@Schema(description = "관람한 공연 id", example = "1")
	private Long concertId;
	// 만약 초기 생성이라면 null
	@Schema(description = "클라코북 Id(클라코북 생성 전이면 null)", example = "1")
	private Long clacoBookId;
	// 관람 날짜
	@NotNull
	@Schema(description = "관람 날짜", example = "2024-11-05")
	private LocalDate watchDate;
	// 관람 회차
	@NotNull
	@Schema(description = "관람 회차", example = "17:00")
	private String watchRound;
	// 관람 좌석
	@Schema(description = "관람 좌석", example = "1층 C열 25번")
	private String watchSit;
	// 별점
	@NotNull
	@Max(5) @Min(0)
	@Schema(description = "별점 (소수점 첫째자리까지)", example = "3.5", minProperties = 0, maxProperties = 5)
	private BigDecimal starRate;
	// 캐스팅
	@NotNull
	@Schema(description = "캐스팅 문자열", example = "고길동, 고희동, 둘리")
	private String casting;
	// 감상평
	@NotNull
	@Size(max = 500)
	@Schema(description = "감상평 본문", example = "공연이 웅장하고 좋았습니다. 추천해요.", maxLength = 500)
	private String content;
	// 장소평
	@NotNull
	@Size(min = 5, max = 5)
	@Schema(description = "장소평 리스트",  minLength = 5, maxLength = 5)
	private List<PlaceCategoryVO> placeReviewIds;
	// 공연 감상 태그
	@NotNull
	@Size(min = 5, max = 5)
	@Schema(description = "공연 감상 리스트", minLength = 5, maxLength = 5)
	private List<TagCategoryVO> tagCategoryIds;

}
