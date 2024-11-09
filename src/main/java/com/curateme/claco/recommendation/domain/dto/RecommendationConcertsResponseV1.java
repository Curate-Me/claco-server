package com.curateme.claco.recommendation.domain.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationConcertsResponseV1 {
    private Long id;
    private String prfnm;
    private String poster;
    private String genrenm;
    private boolean isLiked;

    public RecommendationConcertsResponseV1(Long id, String prfnm, String poster, String genrenm, boolean isLiked) {
        this.id = id;
        this.prfnm = prfnm;
        this.poster = poster;
        this.genrenm = genrenm;
        this.isLiked = isLiked;
    }
}