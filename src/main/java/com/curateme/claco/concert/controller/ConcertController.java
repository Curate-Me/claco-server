package com.curateme.claco.concert.controller;

import com.curateme.claco.concert.domain.dto.response.ConcertAutoCompleteResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertDetailResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertLikedResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertResponseV2;
import com.curateme.claco.concert.service.ConcertService;
import com.curateme.claco.global.annotation.TokenRefreshedCheck;
import com.curateme.claco.global.response.ApiResponse;
import com.curateme.claco.global.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@TokenRefreshedCheck
@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
    public class ConcertController {
        private final ConcertService concertService;

        @GetMapping("/views")
        @Operation(summary = "공연 둘러보기", description = "기능명세서 화면번호 4.0.0")
        @Parameter(name = "genre", description = "장르명", example = "웅장한")
        @Parameter(name = "direction", description = "정렬 순서", example = "asc/dsc")
        public ApiResponse<PageResponse<ConcertResponse>> getConcerts(
            @RequestParam(value = "genre", defaultValue = "all") String genre,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam("page") int page,
            @RequestParam(value = "size", defaultValue = "9") int size) {

            Pageable pageable = PageRequest.of(page - 1, size);
            return ApiResponse.ok(concertService.getConcertInfos(genre, direction, pageable));
        }

    @GetMapping("/filters")
    @Operation(summary = "공연 둘러보기 세부사항 필터", description = "기능명세서 화면번호 4.0.1")
    @Parameter(name = "direction", description = "정렬 순서", example = "asc/dsc")
    @Parameter(name = "area", description = "지역", example = "서울특별시/경기도")
    @Parameter(name = "startDate", description = "시작 날짜", example = "yyyy.MM.dd")
    @Parameter(name = "endDate", description = "끝나는 날짜", example = "yyyy.MM.dd")
    @Parameter(name = "categories", description = "공연 성격 리스트", example = "웅장한, 현대적인(최대 5개)")
    public ApiResponse<PageResponse<ConcertResponse>> filterConcerts(
        @RequestParam(value = "minPrice", defaultValue = "0") Double minPrice,
        @RequestParam(value = "maxPrice", defaultValue = "10000000") Double maxPrice,
        @RequestParam(value = "area", required = false) List<String> area,
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate startDate,
        @RequestParam(value = "endDate", required = false, defaultValue = "9999.12.31") @DateTimeFormat(pattern = "yyyy.MM.dd") LocalDate endDate,
        @RequestParam(value = "direction", defaultValue = "asc") String direction,
        @RequestParam(value = "page") int page,
        @RequestParam(value = "size", defaultValue = "9") int size,
        @RequestParam(value = "categories", defaultValue = "웅장한,섬세한,고전적인,현대적인,서정적인,역동적인,낭만적인,비극적인,친숙한,새로운"
        ) List<String> categories)
    {
        Pageable pageable = PageRequest.of(page - 1, size);

        if (startDate == null) {
            startDate = LocalDate.now();
        }

        if (area == null || area.isEmpty()) {
            area = null;
        }
        return ApiResponse.ok(concertService.getConcertInfosWithFilter(minPrice, maxPrice, area, startDate, endDate, direction, categories, pageable));
    }


        @GetMapping("/queries")
        @Operation(summary = "공연 둘러보기 검색하기", description = "기능명세서 화면번호 4.1.0")
        @Parameter(name = "direction", description = "정렬 순서", example = "asc/dsc")
        @Parameter(name = "query", description = "검색어", required = true)
        public ApiResponse<PageResponse<ConcertResponse>> searchConcerts(
            @RequestParam("query") String query,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam("page") int page,
            @RequestParam(value = "size", defaultValue = "9") int size) {

            Pageable pageable = PageRequest.of(page - 1, size);
            return ApiResponse.ok(concertService.getSearchConcert(query,direction, pageable));
        }

        @GetMapping("/details/{concertId}")
        @Operation(summary = "공연 상세보기", description = "기능명세서 화면번호 3.0.0")
        public ApiResponse<ConcertDetailResponse> getConcertDetails(
            @PathVariable("concertId") Long concertId
        ) {
            return ApiResponse.ok(concertService.getConcertDetailWithCategories(concertId));
        }

        @PostMapping("/likes/{concertId}")
        @Operation(summary = "공연 좋아요", description = "특정 공연에 좋아요를 추가합니다")
        public ApiResponse<String> postLikes(
            @PathVariable("concertId") Long concertId
        ) {
            return ApiResponse.ok(concertService.postLikes(concertId));
        }

        @GetMapping("/likes")
        @Operation(summary = "내가 좋아요한 공연", description = "기능명세서 화면번호 7.3.0")
        @Parameter(name = "query", description = "검색어")
        public ApiResponse<List<ConcertResponseV2>> getMyConcerts(
            @RequestParam(value = "query", required = false, defaultValue = "all") String query,
            @RequestParam(value = "genre", required = false, defaultValue = "all") String genre
        ) {
            return ApiResponse.ok(concertService.getLikedConcert(query, genre));
        }


        @GetMapping("/search")
            @Operation(summary = "자동완성 API", description = "자동완성 기능으로 10개의 공연을 반환")
            public ApiResponse<List<ConcertAutoCompleteResponse>> autoCompletes(
                @RequestParam("query") String query
            ){
                return ApiResponse.ok(concertService.getAutoComplete(query));
            }

        @GetMapping("/reviews/search")
        @Operation(summary = "자동완성 API-리뷰버전", description = "자동완성 기능으로 10개의 공연을 반환")
        public ApiResponse<List<ConcertAutoCompleteResponse>> autoCompletesV2(
            @RequestParam("query") String query
        ){
            return ApiResponse.ok(concertService.getAutoCompleteV2(query));
        }

        @GetMapping("/posters")
        @Operation(summary = "KOPIS Poster", description = "Kopis API Poster 다운로드")
        public ApiResponse<String> getPosters(
            @RequestParam("KopisURL") String KopisURL
        ){
            return ApiResponse.ok(concertService.getS3PosterUrl(KopisURL));
        }
}

