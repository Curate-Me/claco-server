package com.curateme.claco.review.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class TicketReviewSummaryResponse {

    @Schema(name = "사용자 닉네임")
    private String nickName;

    @Schema(name = "공연 제목")
    private String concertName;

    @Schema(name = "공연 아이디")
    private Long concertId;

    @Schema(name = "티켓 등록 날짜(관람 날짜)")
    private LocalDateTime createdAt;

    @Schema(name = "리뷰 내용")
    private String content;

    // Constructor
    public TicketReviewSummaryResponse(String nickName, String concertName, Long concertId, LocalDateTime createdAt, String content) {
        this.nickName = nickName;
        this.concertName = concertName;
        this.concertId = concertId;
        this.createdAt = createdAt;
        this.content = content;
    }
}
