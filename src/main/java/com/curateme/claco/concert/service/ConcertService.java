package com.curateme.claco.concert.service;

import com.curateme.claco.concert.domain.dto.request.ConcertLikesRequest;
import com.curateme.claco.concert.domain.dto.response.ConcertAutoCompleteResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertDetailResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertLikedResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertResponseV2;
import com.curateme.claco.global.response.PageResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface ConcertService {
    PageResponse<ConcertResponse> getConcertInfos(String categoryName, String direction, Pageable pageable);

    PageResponse<ConcertResponse> getConcertInfosWithFilter(Double minPrice, Double maxPrice, List<String> area, LocalDate startDate, LocalDate endDate,
        String direction, List<String> categories, Pageable pageable);

    PageResponse<ConcertResponse> getSearchConcert(String query, String direction, Pageable pageable);

    PageResponse<ConcertResponse> getSearchConcertV2(String query, String direction, Pageable pageable);
    
    ConcertDetailResponse getConcertDetailWithCategories(Long concertId);

    String postLikes(Long concertId);

    List<ConcertResponseV2> getLikedConcert(String query, String genre);

    List<ConcertAutoCompleteResponse> getAutoComplete(String query);

    String getS3PosterUrl(String KopisURL);

    List<ConcertAutoCompleteResponse> getAutoCompleteV2(String query);
}
