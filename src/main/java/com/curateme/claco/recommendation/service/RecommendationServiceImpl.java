package com.curateme.claco.recommendation.service;

import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.clacobook.repository.ClacoBookRepository;
import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.repository.ConcertCategoryRepository;
import com.curateme.claco.concert.repository.ConcertLikeRepository;
import com.curateme.claco.concert.repository.ConcertRepository;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.repository.MemberRepository;
import com.curateme.claco.recommendation.domain.dto.RecommendationConcertResponseV2;
import com.curateme.claco.recommendation.domain.dto.RecommendationConcertResponseV3;
import com.curateme.claco.recommendation.domain.dto.RecommendationConcertsResponseV1;
import com.curateme.claco.review.domain.dto.response.TicketInfoResponse;
import com.curateme.claco.review.domain.dto.response.TicketReviewSummaryResponse;
import com.curateme.claco.review.domain.entity.TicketReview;
import com.curateme.claco.review.repository.TicketReviewRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
public class RecommendationServiceImpl implements RecommendationService{

    private final ConcertRepository concertRepository;
    private final ConcertLikeRepository concertLikeRepository;
    private final ClacoBookRepository clacoBookRepository;
    private final TicketReviewRepository ticketReviewRepository;
    private final ConcertCategoryRepository concertCategoryRepository;
    private final SecurityContextUtil securityContextUtil;
    private final MemberRepository memberRepository;


    @Value("${cloud.ai.url}")
    private String URL;

    // 유저 취향 기반 공연 추천
    @Override
    public List<RecommendationConcertsResponseV1> getConcertRecommendations(int topn) {
        String FLASK_API_URL = URL + "/recommendations/users/";
        // 현재 로그인 세션 유저 정보 추출
        Long memberId = securityContextUtil.getContextMemberInfo().getMemberId();

        String jsonResponse = getConcertsFromFlask(memberId, topn, FLASK_API_URL);

        List<Long> concertIds = parseConcertIdsFromJson(jsonResponse);

        return getConcertDetailsV2(concertIds, memberId);
    }

    // 최근 좋아요한 공연 기반 추천
    @Override
    public RecommendationConcertResponseV3 getLikedConcertRecommendations() {

        Long memberId = securityContextUtil.getContextMemberInfo().getMemberId();

        Pageable pageable = PageRequest.of(0, 1);
        Long concertId = concertLikeRepository.findMostRecentLikedConcert(memberId, pageable)
            .getContent().stream().findFirst().orElse(null);

        List<String> keywords;
        List<RecommendationConcertsResponseV1> recommendedConcerts;

        if (concertId == null) {
            // 상위 3개 공연 가져오기
            Pageable pageable2 = PageRequest.of(0, 3);
            List<Long> concertIds = concertLikeRepository.findTopConcertIdsByLikeCount(pageable2);

            // 첫 번째 콘서트의 ID로 키워드 추출
            if (!concertIds.isEmpty()) {
                Long firstConcertId = concertIds.get(0);
                keywords = concertCategoryRepository.findCategoryNamesByConcertId(firstConcertId).stream()
                    .limit(3)
                    .collect(Collectors.toList());
            } else {
                keywords = Collections.emptyList();
            }

            recommendedConcerts = getConcertDetails(concertIds);
        } else {
            // Flask API 호출하여 추천 데이터 가져오기
            String FLASK_API_URL = URL + "/recommendations/items/";
            int topn = 2;
            String jsonResponse = getConcertsFromFlask(concertId, topn, FLASK_API_URL);

            List<Long> concertIds = parseConcertIdsFromJson(jsonResponse);

            recommendedConcerts = getConcertDetails(concertIds);

            // Use the keywords from the liked concert
            keywords = concertCategoryRepository.findCategoryNamesByConcertId(concertId).stream()
                .limit(3)
                .collect(Collectors.toList());
        }

        return RecommendationConcertResponseV3.builder()
            .likedHistory(concertId != null)
            .keywords(keywords)
            .recommendationConcertsResponseV1s(recommendedConcerts)
            .build();
    }

    // 유저 취향 기반 클라코북 추천
    @Override
    public List<RecommendationConcertResponseV2> getClacoBooksRecommendations() {

        Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
            .findAny()
            .orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

        // Flask API call
        String FLASK_API_URL = URL + "/recommendations/clacobooks/";
        String jsonResponse = getConcertsFromFlaskV2(member.getId(), FLASK_API_URL);

        List<Long> recUserIds = parseConcertIdsFromJson(jsonResponse).stream()
            .limit(3)
            .collect(Collectors.toList());

        List<RecommendationConcertResponseV2> recommendationResponses = new ArrayList<>();

        for (Long recUserId : recUserIds) {
            List<ClacoBook> clacoBooks = clacoBookRepository.findByMemberId(recUserId);

            if (clacoBooks.isEmpty()) {
                continue;
            }

            for (ClacoBook clacoBook : clacoBooks) {
                TicketReview ticketReview = clacoBookRepository.findRandomTicketReviewsByClacoBookId(clacoBook.getId())
                    .stream()
                    .findFirst()
                    .orElse(null);

                if (ticketReview == null) {
                    continue;
                }

                TicketReviewSummaryResponse ticketReviewSummaryResponse = ticketReviewRepository.findSummaryById(ticketReview.getId());

                TicketInfoResponse ticketInfoResponse = TicketInfoResponse.fromEntity(ticketReview);

                recommendationResponses.add(
                    RecommendationConcertResponseV2.from(ticketInfoResponse, ticketReviewSummaryResponse)
                );
            }
        }

        if (recommendationResponses.isEmpty()) {
            ClacoBook randomClacoBook = clacoBookRepository.findRandomClacoBookWithThreeOrMoreReviews()
                .orElseThrow(() -> new BusinessException(ApiStatus.CLACO_BOOK_NOT_FOUND));

            List<TicketReview> randomTicketReviews = clacoBookRepository.findRandomTicketReviewsByClacoBookId(randomClacoBook.getId());

            int limit = Math.min(3, randomTicketReviews.size());
            for (int i = 0; i < limit; i++) {
                TicketReview ticketReview = randomTicketReviews.get(i);

                TicketReviewSummaryResponse ticketReviewSummaryResponse = ticketReviewRepository.findSummaryById(ticketReview.getId());

                TicketInfoResponse ticketInfoResponse = TicketInfoResponse.fromEntity(ticketReview);

                recommendationResponses.add(
                    RecommendationConcertResponseV2.from(ticketInfoResponse, ticketReviewSummaryResponse)
                );
            }
        }

        return recommendationResponses.stream()
            .limit(3)
            .collect(Collectors.toList());
    }



    @Override
    public List<RecommendationConcertsResponseV1> getSearchedConcertRecommendations(Long concertId) {

        List<RecommendationConcertsResponseV1> recommendedConcerts;

        String FLASK_API_URL = URL + "/recommendations/items/";
        int topn = 3;
        String jsonResponse = getConcertsFromFlask(concertId, topn, FLASK_API_URL);

        List<Long> concertIds = parseConcertIdsFromJson(jsonResponse);

        recommendedConcerts = getConcertDetails(concertIds);

        return recommendedConcerts;
    }


    // JSON 응답을 파싱하여 concertIds 리스트 생성
    public List<Long> parseConcertIdsFromJson(String jsonResponse) {
        List<Long> concertIds = new ArrayList<>();
        if (jsonResponse != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(jsonResponse);
                JsonNode recommendationsArray = root.get("recommendations");

                for (JsonNode recommendation : recommendationsArray) {
                    Long concertId = Long.parseLong(recommendation.get(0).asText());
                    concertIds.add(concertId);
                }
            } catch (Exception e) {
            }
        }
        return concertIds;
    }

    // concertIds를 기반으로 콘서트 정보를 조회하여 recommendations 리스트 생성
    public List<RecommendationConcertsResponseV1> getConcertDetails(List<Long> concertIds) {
        List<RecommendationConcertsResponseV1> recommendations = new ArrayList<>();

        for (Long concertId : concertIds) {
            Concert concert = concertRepository.findConcertById(concertId);
            Long id = concert.getId();

            recommendations.add(new RecommendationConcertsResponseV1(
                id,
                concert.getPrfnm(),
                concert.getPoster(),
                concert.getGenrenm(),
                concert.getFcltynm(),
                concert.getPrfpdfrom().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd (EEEE)", Locale.KOREAN)),
                concert.getPrfpdto().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd (EEEE)", Locale.KOREAN))
            ));
        }
        return recommendations;
    }

    public List<RecommendationConcertsResponseV1> getConcertDetailsV2(List<Long> concertIds, Long memberId) {
        List<RecommendationConcertsResponseV1> recommendations = new ArrayList<>();

        for (Long concertId : concertIds) {
            Concert concert = concertRepository.findConcertById(concertId);
            Long id = concert.getId();

            boolean liked = concertLikeRepository.existsByConcertIdAndMemberId(concert.getId(), memberId);

            RecommendationConcertsResponseV1 recommendation = new RecommendationConcertsResponseV1(
                id,
                concert.getPrfnm(),
                concert.getPoster(),
                concert.getGenrenm(),
                concert.getFcltynm(),
                concert.getPrfpdfrom().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd (EEEE)", Locale.KOREAN)),
                concert.getPrfpdto().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd (EEEE)", Locale.KOREAN))
            );

            recommendation.setLiked(liked);
            recommendations.add(recommendation);
        }
        return recommendations;
    }

    public String getConcertsFromFlask(Long Id, int topn, String FLASK_API_URL) {
        String urlWithUserId = FLASK_API_URL + Id + "/" + topn;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlWithUserId, HttpMethod.GET, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return response.getStatusCode().toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String getConcertsFromFlaskV2(Long Id, String FLASK_API_URL) {
        String urlWithUserId = FLASK_API_URL + Id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(urlWithUserId, HttpMethod.GET, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                return response.getStatusCode().toString();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
