package com.curateme.claco.concert.domain.dto.response;

import com.curateme.claco.concert.domain.entity.Concert;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
public class ConcertClacoBookResponse {

    @NotNull
    @Schema(description = "공연 이름")
    private String prfnm;

    @Schema(description = "포스터 URL")
    private String poster;

    @Schema(description = "공연 장소")
    private String fcltynm;

    @Schema(description = "공연 성격 리스트")
    private List<ConcertCategoryResponse> categories;

    public static ConcertClacoBookResponse fromEntity(
        Concert concert, List<ConcertCategoryResponse> categories
    ) {
        return new ConcertClacoBookResponse(concert.getPrfnm(), concert.getPoster(),
            concert.getFcltynm(), categories);
    }
}
