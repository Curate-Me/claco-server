package com.curateme.claco.recommendation.domain.dto;

import com.curateme.claco.review.domain.dto.response.TicketInfoResponse;
import com.curateme.claco.review.domain.dto.response.TicketReviewSummaryResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertClacoBookResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationConcertResponseV2 {

    private TicketInfoResponse ticketInfoResponse;
    private TicketReviewSummaryResponse ticketReviewSummary;

    public static RecommendationConcertResponseV2 from(
        TicketInfoResponse ticketInfoResponse,
        TicketReviewSummaryResponse ticketReviewSummary
    ) {
        return new RecommendationConcertResponseV2(ticketInfoResponse, ticketReviewSummary);
    }
}
