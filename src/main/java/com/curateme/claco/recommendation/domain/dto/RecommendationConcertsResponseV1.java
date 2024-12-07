package com.curateme.claco.recommendation.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecommendationConcertsResponseV1 {

    @Schema(description = "공연 아이디")
    private Long id;

    @Schema(description = "공연 제목")
    private String prfnm;

    @Schema(description = "공연 포스터 URL")
    private String poster;

    @Schema(description = "공연 장르")
    private String genrenm;

    @Column(name = "공연 장소")
    private String fcltynm;

    @Column(name = "start_date")
    private String prfpdfrom;

    @Column(name = "end_date")
    private String prfpdto;

    @Schema(name = "공연 좋아요 여부")
    private Boolean liked;

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public RecommendationConcertsResponseV1(Long id, String prfnm, String poster, String genrenm, String fcltynm, String prfpdfrom, String prfpdto) {
        this.id = id;
        this.prfnm = prfnm;
        this.poster = poster;
        this.genrenm = genrenm;
        this.fcltynm = fcltynm;
        this.prfpdfrom = prfpdfrom;
        this.prfpdto = prfpdto;
    }
}
