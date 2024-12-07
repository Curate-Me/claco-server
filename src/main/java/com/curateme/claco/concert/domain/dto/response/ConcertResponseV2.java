package com.curateme.claco.concert.domain.dto.response;

import com.curateme.claco.concert.domain.entity.Concert;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
public class ConcertResponseV2 {
    @NotNull
    private Long id;

    @NotNull
    @Schema(description = "공연 아이디", example = "PF121682")
    private String mt20id;

    @NotNull
    @Schema(description = "공연 제목", example = "옥탑방 고양이 [대학로]")
    private String prfnm;

    @Schema(description = "공연 시작날짜", example = "2010-04-06")
    private LocalDate prfpdfrom;

    @Schema(description = "공연 종료날짜", example = "2024-11-30")
    private LocalDate prfpdto;

    @Schema(description = "공연 장르", example = "연극")
    private String genrenm;

    @Schema(description = "공연 상태", example = "공연 예정")
    private String prfstate;

    @Schema(description = "공연 포스터")
    private String poster;

    @Schema(description = "공연 시설")
    private String fcltynm;

    private  List<ConcertCategoryResponse> categories;

    @Schema(name = "공연 좋아요 여부", description = "항상 true로 반환")
    private Boolean liked;

    public static ConcertResponseV2 fromEntity(Concert concert, List<ConcertCategoryResponse> categories){
        return new ConcertResponseV2(concert.getId(), concert.getMt20id(), concert.getPrfnm(),
            concert.getPrfpdfrom(), concert.getPrfpdto(), concert.getGenrenm(), concert.getPrfstate(), concert.getPoster(),
            concert.getFcltynm(), categories, true);
    }
}
