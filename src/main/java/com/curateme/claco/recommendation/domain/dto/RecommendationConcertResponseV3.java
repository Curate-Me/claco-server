package com.curateme.claco.recommendation.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationConcertResponseV3 {

    @Schema(description = "좋아요 기록 여부")
    private Boolean likedHistory;

    @Schema(description = "키워드 3개")
    private List<String> keywords;

    @Schema(description = "추천 결과")
    private List<RecommendationConcertsResponseV1> recommendationConcertsResponseV1s;

    public void setLikedHistory(Boolean likedHistory) {
        this.likedHistory = likedHistory;
    }

    public void setRecommendationConcertsResponseV1s(List<RecommendationConcertsResponseV1> concertResponse) {
        this.recommendationConcertsResponseV1s = concertResponse;
    }

}
