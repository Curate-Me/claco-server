package com.curateme.claco.recommendation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.curateme.claco.authentication.domain.JwtMemberDetail;
import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.repository.ConcertCategoryRepository;
import com.curateme.claco.concert.repository.ConcertLikeRepository;
import com.curateme.claco.concert.repository.ConcertRepository;
import com.curateme.claco.recommendation.domain.dto.RecommendationConcertResponseV3;
import com.curateme.claco.recommendation.domain.dto.RecommendationConcertsResponseV1;
import com.curateme.claco.recommendation.service.RecommendationServiceImpl;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceImplTest {

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertLikeRepository concertLikeRepository;

    @Mock
    private ConcertCategoryRepository concertCategoryRepository;

    @Mock
    private RestTemplate restTemplate;

    @Spy
    @InjectMocks
    private RecommendationServiceImpl spyRecommendationService;

    @Mock
    private SecurityContextUtil securityContextUtil;


    @BeforeEach
    void setup() {
        // Mock Concert entities
        lenient().when(concertRepository.findConcertById(1L)).thenReturn(
            Concert.builder()
                .id(1L)
                .prfnm("클래식 콘서트")
                .prfpdfrom(LocalDate.of(2024, 1, 1))
                .prfpdto(LocalDate.of(2024, 12, 31))
                .genrenm("Classical")
                .build()
        );

        lenient().when(concertRepository.findConcertById(2L)).thenReturn(
            Concert.builder()
                .id(2L)
                .prfnm("클래식 콘서트 2")
                .prfpdfrom(LocalDate.of(2024, 1, 1))
                .prfpdto(LocalDate.of(2024, 12, 31))
                .genrenm("Classical")
                .build()
        );
    }



    @Test
    void testGetConcertRecommendations_Success() {

        // Given
        String mockJsonResponse = """
            {
                "recommendations": [
                    [1, 0.9],
                    [2, 0.7]
                ]
            }
        """;

        // RestTemplate Mock 설정
        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class, (mock, context) -> {
            when(mock.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok(mockJsonResponse));
        })) {
            // When
            List<Long> concertIds = recommendationService.parseConcertIdsFromJson(mockJsonResponse);
            List<RecommendationConcertsResponseV1> recommendations = recommendationService.getConcertDetails(concertIds);

            // Then
            assertThat(recommendations).isNotNull();
            assertThat(recommendations).hasSize(2);
            assertThat(recommendations.get(0).getId()).isEqualTo(1L);
            assertThat(recommendations.get(1).getId()).isEqualTo(2L);
        }
    }

    @Test
    void testGetLikedConcertRecommendations_WithLikedConcert() {
        // Given: The user has liked a concert
        when(concertLikeRepository.findMostRecentLikedConcert(eq(1L), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(1L)));

        when(concertCategoryRepository.findCategoryNamesByConcertId(1L))
            .thenReturn(Arrays.asList("Category1", "Category2", "Category3"));

        when(concertRepository.findConcertById(1L)).thenReturn(
            Concert.builder()
                .id(1L)
                .prfnm("Classical Concert")
                .prfpdfrom(LocalDate.of(2024, 1, 1))
                .prfpdto(LocalDate.of(2024, 12, 31))
                .genrenm("Classical")
                .build()
        );

        String mockJsonResponse = """
            {
                "recommendations": [[1, 0.9], [2, 0.8]]
            }
        """;
        // When
        Pageable pageable = PageRequest.of(0, 1);
        Long concertId = concertLikeRepository.findMostRecentLikedConcert(1L, pageable)
            .getContent().stream().findFirst().orElse(null);
        List<Long> concertIds = recommendationService.parseConcertIdsFromJson(mockJsonResponse);
        List<RecommendationConcertsResponseV1> recommendedConcerts = recommendationService.getConcertDetails(concertIds);
        List<String> keywords = concertCategoryRepository.findCategoryNamesByConcertId(concertId);
        // When
        RecommendationConcertResponseV3 response =
            RecommendationConcertResponseV3.builder()
            .likedHistory(true)
            .keywords(keywords)
            .recommendationConcertsResponseV1s(recommendedConcerts)
            .build();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getLikedHistory()).isTrue();
        assertThat(response.getKeywords()).containsExactly("Category1", "Category2", "Category3");
        assertThat(response.getRecommendationConcertsResponseV1s()).hasSize(2);
        assertThat(response.getRecommendationConcertsResponseV1s().get(0).getId()).isEqualTo(1L);
        assertThat(response.getRecommendationConcertsResponseV1s().get(1).getId()).isEqualTo(2L);
    }

    @Test
    void testGetLikedConcertRecommendations_NoLikedConcert() {
        // Given
        when(concertLikeRepository.findTopConcertIdsByLikeCount(any(Pageable.class)))
            .thenReturn(Arrays.asList(1L, 2L));

        when(concertCategoryRepository.findCategoryNamesByConcertId(1L))
            .thenReturn(Arrays.asList("TopCategory1", "TopCategory2"));

        when(concertRepository.findConcertById(1L)).thenReturn(
            Concert.builder()
                .id(1L)
                .prfnm("Classical Concert")
                .prfpdfrom(LocalDate.of(2024, 1, 1))
                .prfpdto(LocalDate.of(2024, 12, 31))
                .genrenm("Classical")
                .build()
        );

        when(concertRepository.findConcertById(2L)).thenReturn(
            Concert.builder()
                .id(2L)
                .prfnm("Pop Concert")
                .prfpdfrom(LocalDate.of(2024, 2, 1))
                .prfpdto(LocalDate.of(2024, 12, 31))
                .genrenm("Pop")
                .build()
        );

        // When
        Pageable pageable = PageRequest.of(0, 3);
        List<Long> topConcertIds = concertLikeRepository.findTopConcertIdsByLikeCount(pageable);
        List<RecommendationConcertsResponseV1> recommendedConcerts = recommendationService.getConcertDetails(topConcertIds);
        List<String> keywords = concertCategoryRepository.findCategoryNamesByConcertId(1L);

        RecommendationConcertResponseV3 response =
            RecommendationConcertResponseV3.builder()
                .likedHistory(false)
                .keywords(keywords)
                .recommendationConcertsResponseV1s(recommendedConcerts)
                .build();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getLikedHistory()).isFalse();
        assertThat(response.getKeywords()).containsExactly("TopCategory1", "TopCategory2");
        assertThat(response.getRecommendationConcertsResponseV1s()).hasSize(2);
        assertThat(response.getRecommendationConcertsResponseV1s().get(0).getId()).isEqualTo(1L);
        assertThat(response.getRecommendationConcertsResponseV1s().get(1).getId()).isEqualTo(2L);
    }

    @Test
    void testGetSearchedConcertRecommendations_Success() {
        // Given
        String mockJsonResponse = """
        {
            "recommendations": [[1, 0.9], [2, 0.8], [3, 0.7]]
        }
    """;

        when(concertRepository.findConcertById(1L)).thenReturn(
            Concert.builder()
                .id(1L)
                .prfnm("Classical Concert")
                .prfpdfrom(LocalDate.of(2024, 1, 1))
                .prfpdto(LocalDate.of(2024, 12, 31))
                .genrenm("Classical")
                .build()
        );

        when(concertRepository.findConcertById(2L)).thenReturn(
            Concert.builder()
                .id(2L)
                .prfnm("Pop Concert")
                .prfpdfrom(LocalDate.of(2024, 2, 1))
                .prfpdto(LocalDate.of(2024, 12, 31))
                .genrenm("Pop")
                .build()
        );

        when(concertRepository.findConcertById(3L)).thenReturn(
            Concert.builder()
                .id(3L)
                .prfnm("Jazz Concert")
                .prfpdfrom(LocalDate.of(2024, 3, 1))
                .prfpdto(LocalDate.of(2024, 12, 31))
                .genrenm("Jazz")
                .build()
        );

        // When
        List<Long> concertIds = recommendationService.parseConcertIdsFromJson(mockJsonResponse);
        List<RecommendationConcertsResponseV1> recommendations = recommendationService.getConcertDetails(concertIds);

        // Then
        assertThat(recommendations).isNotNull();
        assertThat(recommendations).hasSize(3);
        assertThat(recommendations.get(0).getId()).isEqualTo(1L);
        assertThat(recommendations.get(1).getId()).isEqualTo(2L);
        assertThat(recommendations.get(2).getId()).isEqualTo(3L);
    }

    /*
    @Test
    void testGetConcertsFromFlaskSuccess() {
        // Given
        String FLASK_API_URL = "http://43.203.228.177:5000/recommendations/users/";
        Long id = 1L;
        int topn = 3;
        String expectedResponse = "{\"recommendations\":[[\"101\"],[\"102\"],[\"103\"]]}";

        // Mock RestTemplate 동작
        String fullUrl = FLASK_API_URL + id + "/" + topn;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> mockResponse = ResponseEntity.ok(expectedResponse);
        lenient().when(restTemplate.exchange(eq(fullUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        // When
        String actualResponse = recommendationService.getConcertsFromFlask(id, topn, FLASK_API_URL);

        // Then
        assertThat(actualResponse).isNotNull();
    }

     */

    /*
    @Test
    void testGetConcertsFromFlaskV2Success() {
        // Given
        String FLASK_API_URL = "http://43.203.228.177:5000/recommendations/clacobooks/";
        Long id = 1L;
        String expectedResponse = "{\"recommendations\":[[\"201\"],[\"202\"]]}";

        // Mock RestTemplate 동작
        String fullUrl = FLASK_API_URL + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> mockResponse = ResponseEntity.ok(expectedResponse);
        lenient().when(restTemplate.exchange(eq(fullUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenReturn(mockResponse);

        // When
        String actualResponse = recommendationService.getConcertsFromFlaskV2(id, FLASK_API_URL);

        // Then
        assertThat(actualResponse).isNotNull();
    }

     */

    @Test
    void testGetSearchedConcertRecommendations() {
        // Given
        Long concertId = 1L;
        String FLASK_API_URL = "http://mock-api.com/recommendations/items/";
        String jsonResponse = "{\"recommendations\":[[\"101\"],[\"102\"],[\"103\"]]}";
        List<Long> concertIds = List.of(101L, 102L, 103L);
        List<RecommendationConcertsResponseV1> mockRecommendations = List.of(
            new RecommendationConcertsResponseV1(101L, "Mock Concert 101", "poster1.jpg", "Genre1", "Venue1", "2024-01-01", "2024-01-31"),
            new RecommendationConcertsResponseV1(102L, "Mock Concert 102", "poster2.jpg", "Genre2", "Venue2", "2024-02-01", "2024-02-28"),
            new RecommendationConcertsResponseV1(103L, "Mock Concert 103", "poster3.jpg", "Genre3", "Venue3", "2024-03-01", "2024-03-31")
        );

        // Mocking 내부 메서드 호출
        lenient().doReturn(jsonResponse).when(spyRecommendationService).getConcertsFromFlask(eq(concertId), eq(3), eq(FLASK_API_URL));
        lenient().doReturn(concertIds).when(spyRecommendationService).parseConcertIdsFromJson(eq(jsonResponse));
        lenient().doReturn(mockRecommendations).when(spyRecommendationService).getConcertDetails(eq(concertIds));

        // When
        List<RecommendationConcertsResponseV1> result = spyRecommendationService.getSearchedConcertRecommendations(concertId);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void testGetConcertRecommendations() {
        // Given
        Long memberId = 1L;
        String FLASK_API_URL = "http://mock-api.com/recommendations/users/";
        String jsonResponse = "{\"recommendations\":[[\"101\"],[\"102\"]]}";
        List<Long> concertIds = List.of(101L, 102L);
        List<RecommendationConcertsResponseV1> mockRecommendations = List.of(
            new RecommendationConcertsResponseV1(101L, "Mock Concert 101", "poster1.jpg", "Genre1", "Venue1", "2024-01-01", "2024-01-31"),
            new RecommendationConcertsResponseV1(102L, "Mock Concert 102", "poster2.jpg", "Genre2", "Venue2", "2024-02-01", "2024-02-28")
        );
        JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);


        // Mock 내부 메서드 호출
        when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
        when(jwtMemberDetailMock.getMemberId()).thenReturn(memberId);
        lenient().doReturn(jsonResponse).when(spyRecommendationService).getConcertsFromFlask(eq(memberId), eq(2), eq(FLASK_API_URL));
        lenient().doReturn(concertIds).when(spyRecommendationService).parseConcertIdsFromJson(eq(jsonResponse));
        lenient().doReturn(mockRecommendations).when(spyRecommendationService).getConcertDetails(eq(concertIds));

        // When
        List<RecommendationConcertsResponseV1> result = spyRecommendationService.getConcertRecommendations(5);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void testGetLikedConcertRecommendations() {
        // Given
        Long memberId = 1L;
        Long concertId = 101L;
        String FLASK_API_URL = "http://mock-api.com/recommendations/items/";
        String jsonResponse = "{\"recommendations\":[[\"102\"],[\"103\"]]}";
        List<Long> concertIds = List.of(102L, 103L);
        List<String> keywords = List.of("Keyword1", "Keyword2");
        List<RecommendationConcertsResponseV1> mockRecommendations = List.of(
            new RecommendationConcertsResponseV1(102L, "Mock Concert 102", "poster2.jpg", "Genre2", "Venue2", "2024-02-01", "2024-02-28"),
            new RecommendationConcertsResponseV1(103L, "Mock Concert 103", "poster3.jpg", "Genre3", "Venue3", "2024-03-01", "2024-03-31")
        );

        // Mock 내부 메서드 호출
        JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);


        // Mock 내부 메서드 호출
        when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
        when(jwtMemberDetailMock.getMemberId()).thenReturn(memberId);
        lenient().when(concertLikeRepository.findMostRecentLikedConcert(eq(memberId), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(concertId)));
        lenient().doReturn(jsonResponse).when(spyRecommendationService).getConcertsFromFlask(eq(concertId), eq(2), eq(FLASK_API_URL));
        lenient().doReturn(concertIds).when(spyRecommendationService).parseConcertIdsFromJson(eq(jsonResponse));
        lenient().doReturn(mockRecommendations).when(spyRecommendationService).getConcertDetails(eq(concertIds));
        lenient().when(concertCategoryRepository.findCategoryNamesByConcertId(eq(concertId))).thenReturn(keywords);

        // When
        RecommendationConcertResponseV3 result = spyRecommendationService.getLikedConcertRecommendations();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLikedHistory()).isTrue();
        assertThat(result.getKeywords()).isEqualTo(keywords);
    }

}

