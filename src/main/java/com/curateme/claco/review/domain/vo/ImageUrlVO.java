package com.curateme.claco.review.domain.vo;

import com.curateme.claco.review.domain.entity.ReviewImage;
import com.curateme.claco.review.domain.entity.TicketReview;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.11.03
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.03		   이 건		   최초 생성
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageUrlVO {

	// 이미지 주소
	@Schema(description = "이미지 Url", example = "https://claco.com")
	private String imageUrl;

	public static ImageUrlVO fromReviewImage(ReviewImage image) {
		return new ImageUrlVO(image.getImageUrl());
	}

	public static ImageUrlVO fromTicketImage(TicketReview ticketReview) {
		return new ImageUrlVO(ticketReview.getTicketImage());
	}

}
