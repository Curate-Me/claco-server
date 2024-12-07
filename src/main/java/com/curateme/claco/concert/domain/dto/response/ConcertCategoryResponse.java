package com.curateme.claco.concert.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertCategoryResponse {

    @Schema(description = "공연 성격")
    private String category;

    @Schema(description = "성격 이미지 URL")
    private String imageURL;
}
