package com.curateme.claco.review.domain.dto;

import java.math.BigDecimal;

import com.curateme.claco.review.domain.entity.TicketReview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
public class TicketReviewUpdateDto {

	// TicketReview id
	@NotNull
	@Schema(description = "티켓리뷰 id", example = "1")
	private Long ticketReviewId;
	// 관람 좌석
	@Schema(description = "관람 좌석", example = "1층 2열")
	private String watchSit;
	// 별점
	@Schema(description = "별점", example = "1.5")
	private BigDecimal starRate;
	// 감상평
	@Schema(description = "감상평", example = "공연이 재미있어요.")
	private String content;

	public static TicketReviewUpdateDto fromEntity(TicketReview ticketReview) {
		return new TicketReviewUpdateDto(ticketReview.getId(), ticketReview.getWatchSit(), ticketReview.getStarRate(),
			ticketReview.getContent());
	}

}
