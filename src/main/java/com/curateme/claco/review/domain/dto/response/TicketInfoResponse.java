package com.curateme.claco.review.domain.dto.response;

import com.curateme.claco.review.domain.entity.TicketReview;

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
public class TicketInfoResponse {

	@Schema(description = "티켓의 Id", example = "1")
	private Long id;
	@Schema(description = "티켓의 이미지 url", example = "https://claco.com")
	private String ticketImage;

	public static TicketInfoResponse fromEntity(TicketReview ticketReview) {
		return new TicketInfoResponse(ticketReview.getId(), ticketReview.getTicketImage());
	}

}
