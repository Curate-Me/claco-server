package com.curateme.claco.review.domain.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.curateme.claco.review.domain.entity.PlaceReview;
import com.curateme.claco.review.domain.entity.ReviewTag;
import com.curateme.claco.review.domain.entity.TicketReview;
import com.curateme.claco.review.domain.vo.ImageUrlVO;
import com.curateme.claco.review.domain.vo.PlaceCategoryVO;
import com.curateme.claco.review.domain.vo.TagCategoryVO;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketReviewInfoResponse {

	// 티켓 리뷰 id
	@Schema(description = "티켓리뷰 Id", example = "1")
	private Long ticketReviewId;
	// 콘서트 명
	@Schema(description = "콘서트 명", example = "오페라의 유령")
	private String concertName;
	@Schema(description = "닉네임, create 응답에는 미포함", example = "사용자1")
	private String nickname;
	// 관람 날짜
	@Schema(description = "관람 날짜")
	private LocalDate watchDate;
	// 리뷰 남긴 날짜
	@Schema(description = "리뷰 남긴 날짜")
	private LocalDate createdDate;
	// 관람 위치(공연장)
	@Schema(description = "관람 위치(공연장)", example = "예술의 전당")
	private String watchPlace;
	// 관람 회차
	@Schema(description = "관람 회차", example = "17:00")
	private String watchRound;
	// 러닝 타임
	@Schema(description = "러닝 타임", example = "150분")
	private String runningTime;
	// 캐스팅
	@Schema(description = "캐스팅", example = "고길동, 고희동")
	private String castings;
	// 관람 좌석
	@Schema(description = "관람 좌석", example = "1층 3열")
	private String watchSit;
	@Schema(description = "티켓 이미지 url", example = "https://claco-server.com/image")
	private String ticketImage;
	// 관람 태그(공연 성격)
	@Schema(description = "공연 성격들")
	private List<TagCategoryVO> concertTags;
	// 별점
	@Schema(description = "별점", example = "3.5")
	private BigDecimal starRate;
	// 관람평(본문)
	@Schema(description = "감상평", example = "공연이 재미있어요.")
	private String content;
	@Schema(description = "클라코 북 id", example = "1")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long clacoBookId;
	@Schema(description = "콘서트 장르 이름", example = "무용")
	private String genreName;
	@Schema(description = "공연 중인 여부", example = "공연 중")
	private String concertState;
	// 장소평
	@Schema(description = "장소평들")
	private List<PlaceCategoryVO> placeReviews;
	// 리뷰 이미지
	@Schema(description = "리뷰 이미지들")
	private List<ImageUrlVO> imageUrlS;
	@Builder.Default
	@Schema(description = "티켓 리뷰 소유주")
	private Boolean editor = true;

	public void updateClacoBookId(Long clacoBookId) {
		this.clacoBookId = clacoBookId;
	}

	public static TicketReviewInfoResponse fromTicketReview(TicketReview ticketReview) {
		TicketReviewInfoResponse response = new TicketReviewInfoResponse();
		response.ticketReviewId = ticketReview.getId();
		response.nickname = ticketReview.getMember().getNickname();
		response.watchDate = ticketReview.getWatchDate();
		response.watchRound = ticketReview.getWatchRound();
		response.watchSit = ticketReview.getWatchSit();
		response.ticketImage = ticketReview.getTicketImage();
		response.concertTags = ticketReview.getReviewTags().stream()
			.map(ReviewTag::getTagCategory)
			.map(TagCategoryVO::fromEntity)
			.toList();
		response.starRate = ticketReview.getStarRate();
		response.content = ticketReview.getContent();
		response.placeReviews = ticketReview.getPlaceReviews()
			.stream()
			.map(PlaceReview::getPlaceCategory)
			.map(PlaceCategoryVO::fromEntity)
			.toList();
		response.imageUrlS = ticketReview.getReviewImages().stream().map(ImageUrlVO::fromReviewImage).toList();
		response.concertName = ticketReview.getConcert().getPrfnm();
		response.watchPlace = ticketReview.getConcert().getFcltynm();
		response.runningTime = ticketReview.getConcert().getPrfruntime();
		response.castings = ticketReview.getCasting();
		response.createdDate = LocalDate.from(ticketReview.getCreatedAt());
		response.genreName = ticketReview.getConcert().getGenrenm();
		response.concertState = ticketReview.getConcert().getPrfstate();

		return response;
	}



}
