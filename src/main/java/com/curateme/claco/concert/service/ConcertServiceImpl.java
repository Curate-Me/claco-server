package com.curateme.claco.concert.service;

import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.concert.domain.dto.request.ConcertLikesRequest;
import com.curateme.claco.concert.domain.dto.response.ConcertAutoCompleteResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertCategoryResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertDetailResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertLikedResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertResponse;
import com.curateme.claco.concert.domain.dto.response.ConcertResponseV2;
import com.curateme.claco.concert.domain.entity.Category;
import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.domain.entity.ConcertLike;
import com.curateme.claco.concert.repository.CategoryRepository;
import com.curateme.claco.concert.repository.ConcertCategoryRepository;
import com.curateme.claco.concert.repository.ConcertLikeRepository;
import com.curateme.claco.concert.repository.ConcertRepository;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.global.response.PageResponse;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.repository.MemberRepository;
import com.curateme.claco.recommendation.domain.dto.RecommendationConcertsResponseV1;
import com.curateme.claco.recommendation.service.RecommendationService;
import com.curateme.claco.recommendation.service.RecommendationServiceImpl;
import com.curateme.claco.review.domain.dto.response.TicketReviewSimpleResponse;
import com.curateme.claco.review.domain.entity.TicketReview;
import com.curateme.claco.review.repository.TicketReviewRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final ConcertCategoryRepository concertCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final ConcertLikeRepository concertLikeRepository;
    private final SecurityContextUtil securityContextUtil;
    private final TicketReviewRepository ticketReviewRepository;
    private final RecommendationServiceImpl recommendationServiceImpl;

    @Value("${cloud.ai.url}")
    private String URL;


    @Override
    public PageResponse<ConcertResponse> getConcertInfos(String genre, String direction, Pageable pageable) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by("prfpdfrom").ascending() : Sort.by("prfpdfrom").descending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Concert> concertPage = concertRepository.findConcertsByGenreWithPagination(genre, sortedPageable);
        Long memberId = securityContextUtil.getContextMemberInfo().getMemberId();

        List<ConcertResponse> concertResponses = concertPage.getContent().stream()
            .map(concert -> {
                List<Long> categoryIds = concertCategoryRepository.findCategoryIdsByCategoryName(concert.getId());
                List<Category> categoryList = categoryRepository.findAllById(categoryIds);

                List<ConcertCategoryResponse> categoryResponses = categoryList.stream()
                    .map(category -> new ConcertCategoryResponse(category.getCategory(), category.getImageUrl()))
                    .collect(Collectors.toList());

                ConcertResponse response = ConcertResponse.fromEntity(concert, categoryResponses, null);

                // 좋아요 여부 설정
                boolean liked = concertLikeRepository.existsByConcertIdAndMemberId(concert.getId(), memberId);
                response.setLiked(liked);

                return response;
            })
            .collect(Collectors.toList());

        return PageResponse.<ConcertResponse>builder()
            .listPageResponse(concertResponses)
            .totalCount(concertPage.getTotalElements())
            .size(concertPage.getSize())
            .totalPage(concertPage.getTotalPages())
            .currentPage(concertPage.getPageable().getPageNumber()+1)
            .build();
    }

    @Override
    public PageResponse<ConcertResponse> getConcertInfosWithFilter(Double minPrice, Double maxPrice,
        List<String> area, LocalDate startDate, LocalDate endDate, String direction, List<String> categories, Pageable pageable) {

        Comparator<Concert> comparator = direction.equalsIgnoreCase("asc")
            ? Comparator.comparing(Concert::getPrfpdfrom)
            : Comparator.comparing(Concert::getPrfpdfrom).reversed();

        List<Concert> concertList = new ArrayList<>(concertRepository.findConcertsByFiltersWithoutPaging(area, startDate, endDate, categories));

        concertList.sort(comparator);

        long totalElements = concertList.size();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), concertList.size());
        int pageSize = pageable.getPageSize();
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        List<Concert> paginatedConcerts = concertList.subList(start, end);

        List<ConcertResponse> concertResponses = paginatedConcerts.stream()
            .map(concert -> {
                List<Long> categoryIds = concertCategoryRepository.findCategoryIdsByCategoryName(concert.getId());
                List<Category> categoryList = categoryRepository.findAllById(categoryIds);

                List<ConcertCategoryResponse> categoryResponses = categoryList.stream()
                    .map(category -> new ConcertCategoryResponse(category.getCategory(), category.getImageUrl()))
                    .collect(Collectors.toList());

                return ConcertResponse.fromEntity(concert, categoryResponses,null);
            })
            .collect(Collectors.toList());

        // PageResponse 생성 및 반환
        return PageResponse.<ConcertResponse>builder()
            .listPageResponse(concertResponses)
            .totalCount(totalElements)
            .size(pageable.getPageSize())
            .totalPage(totalPages)
            .currentPage(pageable.getPageNumber()+1)
            .build();
    }


    @Override
    public PageResponse<ConcertResponse> getSearchConcert(String query, String direction, Pageable pageable) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by("prfpdfrom").ascending() : Sort.by("prfpdfrom").descending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Concert> concertPage = concertRepository.findBySearchQuery(query, sortedPageable);

        List<ConcertResponse> concertResponses;

        if (concertPage.isEmpty()) { // `concertPage`가 비어 있는 경우 처리
            List<RecommendationConcertsResponseV1> recommendationConcertsResponseV1s = recommendationServiceImpl.getConcertRecommendations(3);
            concertResponses = List.of(ConcertResponse.fromRecommendations(recommendationConcertsResponseV1s));
        } else {
            concertResponses = concertPage.getContent().stream()
                .map(concert -> {
                    List<Long> categoryIds = concertCategoryRepository.findCategoryIdsByCategoryName(concert.getId());
                    List<Category> categories = categoryRepository.findAllById(categoryIds);

                    List<ConcertCategoryResponse> categoryResponses = categories.stream()
                        .map(category -> new ConcertCategoryResponse(category.getCategory(), category.getImageUrl()))
                        .collect(Collectors.toList());

                    return ConcertResponse.fromEntity(concert, categoryResponses, null);
                })
                .collect(Collectors.toList());
        }

        // PageResponse 생성
        return PageResponse.<ConcertResponse>builder()
            .listPageResponse(concertResponses)
            .totalCount(concertPage.getTotalElements())
            .size(concertPage.getSize())
            .totalPage(concertPage.getTotalPages())
            .currentPage(concertPage.getPageable().getPageNumber()+1)
            .build();
    }

    @Override
    public PageResponse<ConcertResponse> getSearchConcertV2(String query, String direction, Pageable pageable) {

        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by("prfpdfrom").ascending() : Sort.by("prfpdfrom").descending();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Concert> concertPage = concertRepository.findBySearchQueryV2(query, sortedPageable);

        List<ConcertResponse> concertResponses;

        if (concertPage.isEmpty()) { // `concertPage`가 비어 있는 경우 처리
            List<RecommendationConcertsResponseV1> recommendationConcertsResponseV1s = recommendationServiceImpl.getConcertRecommendations(3);
            concertResponses = List.of(ConcertResponse.fromRecommendations(recommendationConcertsResponseV1s));
        } else {
            concertResponses = concertPage.getContent().stream()
                .map(concert -> {
                    List<Long> categoryIds = concertCategoryRepository.findCategoryIdsByCategoryName(concert.getId());
                    List<Category> categories = categoryRepository.findAllById(categoryIds);

                    List<ConcertCategoryResponse> categoryResponses = categories.stream()
                        .map(category -> new ConcertCategoryResponse(category.getCategory(), category.getImageUrl()))
                        .collect(Collectors.toList());

                    return ConcertResponse.fromEntity(concert, categoryResponses, null);
                })
                .collect(Collectors.toList());
        }

        // PageResponse 생성
        return PageResponse.<ConcertResponse>builder()
            .listPageResponse(concertResponses)
            .totalCount(concertPage.getTotalElements())
            .size(concertPage.getSize())
            .totalPage(concertPage.getTotalPages())
            .currentPage(concertPage.getPageable().getPageNumber()+1)
            .build();
    }


    @Override
    public ConcertDetailResponse getConcertDetailWithCategories(Long concertId) {

        Long memberId = securityContextUtil.getContextMemberInfo().getMemberId();

        Concert concert = concertRepository.findConcertById(concertId);

        List<Long> ticketReviewIds =  ticketReviewRepository.findByConcertId(concertId);

        List<TicketReview> ticketReviews = ticketReviewRepository.findAllById(ticketReviewIds);

        List<TicketReviewSimpleResponse> ticketReviewResponses = ticketReviews.stream()
            .map(TicketReviewSimpleResponse::fromEntity)
            .collect(Collectors.toList());

        List<Long> categoryIds = concertCategoryRepository.findCategoryIdsByCategoryName(concertId);
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        List<ConcertCategoryResponse> categoryResponses = categories.stream()
            .map(category -> new ConcertCategoryResponse(category.getCategory(), category.getImageUrl()))
            .collect(Collectors.toList());

        boolean liked = concertLikeRepository.existsByConcertIdAndMemberId(concertId, memberId);

        return ConcertDetailResponse.fromEntity(concert, ticketReviewResponses, categoryResponses, liked);
    }

    @Override
    public String postLikes(Long concertId) {

        Long memberId = securityContextUtil.getContextMemberInfo().getMemberId();
        Member member = memberRepository.getById(memberId);
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> new BusinessException(ApiStatus.CONCERT_NOT_FOUND));

        // 좋아요가 이미 있는지 확인
        Optional<ConcertLike> existingLike = concertLikeRepository.findByMemberAndConcert(member, concert);

        if (existingLike.isPresent()) {
            concertLikeRepository.delete(existingLike.get());
            return "좋아요가 취소되었습니다.";
        } else {
            ConcertLike concertLike = ConcertLike.builder()
                .member(member)
                .concert(concert)
                .build();
            concertLikeRepository.save(concertLike);
            return "좋아요가 등록되었습니다.";
        }
    }

    @Override
    public List<ConcertResponseV2> getLikedConcert(String query, String genre) {

        Long memberId = securityContextUtil.getContextMemberInfo().getMemberId();

        List<Long> concertLikedIds = concertLikeRepository.findConcertIdsByMemberId(memberId);

        // 필터링 적용
        concertLikedIds = filterConcertsByQueryAndGenre(concertLikedIds, query, genre);

        List<ConcertResponseV2> likedConcerts = new ArrayList<>();

        concertLikedIds.forEach(concertId -> {
            Concert concert = concertRepository.findConcertById(concertId);

            List<Long> categoryIds = concertCategoryRepository.findCategoryIdsByCategoryName(concertId);
            List<Category> categories = categoryRepository.findAllById(categoryIds);

            List<ConcertCategoryResponse> categoryResponses = categories.stream()
                .map(category -> new ConcertCategoryResponse(category.getCategory(), category.getImageUrl()))
                .collect(Collectors.toList());

            ConcertResponseV2 response = ConcertResponseV2.fromEntity(concert, categoryResponses);
            likedConcerts.add(response);
        });


        return likedConcerts;
    }

    @Override
    public List<ConcertAutoCompleteResponse> getAutoComplete(String query) {

        List<Long> concertIds = concertRepository.findConcertIdsBySearchQuery(query);
        List<Long> topConcertIds = concertIds.size() > 10 ? concertIds.subList(0, 10) : concertIds;

        List<Concert> concerts = concertRepository.findAllById(topConcertIds);

        return concerts.stream()
            .map(ConcertAutoCompleteResponse::fromEntity)
            .toList();
    }

    @Override
    public List<ConcertAutoCompleteResponse> getAutoCompleteV2(String query) {

        List<Long> concertIds = concertRepository.findConcertIdsBySearchQueryV2(query);
        List<Long> topConcertIds = concertIds.size() > 10 ? concertIds.subList(0, 10) : concertIds;

        List<Concert> concerts = concertRepository.findAllById(topConcertIds);

        return concerts.stream()
            .map(ConcertAutoCompleteResponse::fromEntity)
            .toList();
    }

    public String getS3PosterUrl(String KopisURL) {
        String urlWithUserId = URL + "/download/posters";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("image_url", KopisURL);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {

            ResponseEntity<String> response = restTemplate.exchange(urlWithUserId, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());

                return rootNode.get("s3_url").asText();
            } else {
                return response.getBody();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    List<Long> filterConcertsByQueryAndGenre(List<Long> concertLikedIds, String query, String genre) {
        // 검색어로 필터링
        if (query != null && !query.isEmpty()) {
            List<Long> filteredByQuery = concertRepository.findConcertIdsBySearchQuery(query);
            concertLikedIds = concertLikedIds.stream()
                .filter(filteredByQuery::contains)
                .toList();
            
        }

        // 장르로 필터링
        if (!"all".equals(genre) && !genre.isEmpty()) {
            concertLikedIds = concertLikedIds.stream()
                .filter(concertId -> {
                    Concert concert = concertRepository.findConcertById(concertId);
                    return genre.equals(concert.getGenrenm());
                })
                .collect(Collectors.toList());
        }

        return concertLikedIds;
    }




}

