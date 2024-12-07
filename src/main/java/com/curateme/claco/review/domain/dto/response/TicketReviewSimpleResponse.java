package com.curateme.claco.review.domain.dto.response;

import com.curateme.claco.review.domain.entity.TicketReview;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketReviewSimpleResponse {

    @Schema(description = "티켓리뷰 Id", example = "1")
    private Long ticketReviewId;

    @Schema(description = "닉네임, create 응답에는 미포함", example = "사용자1")
    private String nickname;

    @Schema(description = "별점", example = "3.5")
    private BigDecimal starRate;

    @Schema(description = "감상평", example = "공연이 재미있어요.")
    private String content;

    public static TicketReviewSimpleResponse fromEntity(TicketReview ticketReview) {
        return TicketReviewSimpleResponse.builder()
            .ticketReviewId(ticketReview.getId())
            .nickname(ticketReview.getMember().getNickname())
            .starRate(ticketReview.getStarRate())
            .content(ticketReview.getContent())
            .build();
    }

}
