package com.curateme.claco.concert.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.curateme.claco.authentication.domain.JwtMemberDetail;
import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.concert.domain.dto.response.*;
import com.curateme.claco.concert.domain.entity.*;
import com.curateme.claco.concert.repository.*;
import com.curateme.claco.global.response.PageResponse;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.repository.MemberRepository;
import com.curateme.claco.review.domain.entity.TicketReview;
import com.curateme.claco.review.repository.TicketReviewRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import com.curateme.claco.member.domain.entity.Role;


import java.time.LocalDate;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class ConcertServiceTest {

    @Mock private ConcertRepository concertRepository;
    @Mock private ConcertCategoryRepository concertCategoryRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private SecurityContextUtil securityContextUtil;
    @Mock private ConcertLikeRepository concertLikeRepository;
    @Mock private TicketReviewRepository ticketReviewRepository;

    @InjectMocks private ConcertServiceImpl concertService;

    private final Pageable pageable = PageRequest.of(0, 10, Sort.by("prfpdfrom").ascending());

    @Test
    @DisplayName("장르 기반 콘서트 조회")
    void testGetConcertInfos() {

        Long memberId = 10L;

        JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

        // Given
        String genre = "Classical";
        Concert mockConcert = Concert.builder()
            .id(1L)
            .prfnm("클래식 콘서트")
            .prfpdfrom(LocalDate.of(2024, 1, 1))
            .prfpdto(LocalDate.of(2024, 12, 31))
            .genrenm("Classical")
            .build();

        when(concertRepository.findConcertsByGenreWithPagination(eq(genre), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(mockConcert), pageable, 1));

        when(concertCategoryRepository.findCategoryIdsByCategoryName(1L))
            .thenReturn(List.of(1L));
        when(categoryRepository.findAllById(List.of(1L)))
            .thenReturn(List.of(
                Category.builder().id(1L).category("웅장한").imageUrl("image-url-1").build()
            ));
        when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
        when(jwtMemberDetailMock.getMemberId()).thenReturn(memberId);

        // When
        PageResponse<ConcertResponse> result = concertService.getConcertInfos(genre, "asc", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getListPageResponse()).hasSize(1);
        assertThat(result.getListPageResponse().get(0).getPrfnm()).isEqualTo("클래식 콘서트");
        verify(concertRepository, times(1)).findConcertsByGenreWithPagination(eq(genre), any(Pageable.class));
    }

    @Test
    @DisplayName("필터 기반 콘서트 조회")
    void testGetConcertInfosWithFilter() {
        // Given
        List<String> categories = List.of("웅장한");
        Concert mockConcert = Concert.builder()
            .id(1L)
            .prfnm("테스트 콘서트")
            .area("서울특별시")
            .prfpdfrom(LocalDate.of(2024, 1, 1))
            .prfpdto(LocalDate.of(2024, 12, 31))
            .build();

        // Mock repository responses
        when(concertRepository.findConcertsByFiltersWithoutPaging(
            eq(null),
            eq(LocalDate.of(2023, 1, 1)),
            eq(LocalDate.of(2024, 12, 31)),
            eq(categories)))
            .thenReturn(List.of(mockConcert));

        when(concertCategoryRepository.findCategoryIdsByCategoryName(1L))
            .thenReturn(List.of(1L));

        when(categoryRepository.findAllById(List.of(1L)))
            .thenReturn(List.of(
                Category.builder().id(1L).category("웅장한").imageUrl("image-url-1").build()
            ));

        // Mock pageable
        Pageable pageable = PageRequest.of(0, 10);

        // When
        PageResponse<ConcertResponse> result = concertService.getConcertInfosWithFilter(
            0.0, 100.0, null,
            LocalDate.of(2023, 1, 1), LocalDate.of(2024, 12, 31),
            "asc", categories, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getListPageResponse()).hasSize(1);
        assertThat(result.getListPageResponse().get(0).getPrfnm()).isEqualTo("테스트 콘서트");
        assertThat(result.getListPageResponse().get(0).getCategories()).hasSize(1);
        assertThat(result.getListPageResponse().get(0).getCategories().get(0).getCategory()).isEqualTo("웅장한");
    }


    @Test
    void testGetConcertDetailWithCategories() {
        // Given
        Long concertId = 1L;
        Long memberId = 10L;

        JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

        Member testMember = Member.builder()
            .id(memberId)
            .nickname("test_user")
            .email("test@test.com")
            .role(Role.MEMBER)
            .build();

        Concert mockConcert = Concert.builder()
            .id(concertId)
            .prfnm("테스트 콘서트")
            .genrenm("Classical")
            .build();

        TicketReview mockReview = TicketReview.builder()
            .id(1L)
            .starRate(BigDecimal.valueOf(4.5))
            .member(testMember)
            .build();

        // Mock Category 객체 생성
        Category mockCategory = mock(Category.class);
        lenient().when(mockCategory.getId()).thenReturn(1L);
        lenient().when(mockCategory.getCategory()).thenReturn("웅장한");
        lenient().when(mockCategory.getImageUrl()).thenReturn("image-url-1");

        // Mock 설정
        when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
        when(jwtMemberDetailMock.getMemberId()).thenReturn(memberId);
        when(concertRepository.findConcertById(concertId)).thenReturn(mockConcert);
        when(ticketReviewRepository.findByConcertId(concertId)).thenReturn(List.of(mockReview.getId()));
        when(ticketReviewRepository.findAllById(List.of(mockReview.getId()))).thenReturn(List.of(mockReview));
        when(concertCategoryRepository.findCategoryIdsByCategoryName(concertId)).thenReturn(List.of(1L));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(mockCategory));
        when(concertLikeRepository.existsByConcertIdAndMemberId(concertId, memberId)).thenReturn(true);

        // When
        ConcertDetailResponse result = concertService.getConcertDetailWithCategories(concertId);

        // Then
        verify(securityContextUtil).getContextMemberInfo();
        verify(jwtMemberDetailMock).getMemberId();
        verify(concertRepository).findConcertById(concertId);
        verify(ticketReviewRepository).findByConcertId(concertId);
        verify(ticketReviewRepository).findAllById(List.of(mockReview.getId()));
        verify(concertCategoryRepository).findCategoryIdsByCategoryName(concertId);
        verify(categoryRepository).findAllById(List.of(1L));
        verify(concertLikeRepository).existsByConcertIdAndMemberId(concertId, memberId);

        assertThat(result).isNotNull();
        assertThat(result.getPrfnm()).isEqualTo("테스트 콘서트");
        assertThat(result.getCategories()).hasSize(1);
        assertThat(result.getCategories().get(0).getCategory()).isEqualTo("웅장한");
        assertThat(result.getCategories().get(0).getImageURL()).isEqualTo("image-url-1");
        assertThat(result.isLiked()).isTrue();
    }


    @Test
    @DisplayName("콘서트 자동 완성 결과 조회")
    void testGetAutoComplete() {
        // Given
        String query = "클래식";
        Concert mockConcert1 = Concert.builder()
            .id(1L)
            .prfnm("클래식 콘서트 1")
            .build();

        Concert mockConcert2 = Concert.builder()
            .id(2L)
            .prfnm("클래식 콘서트 2")
            .build();

        when(concertRepository.findConcertIdsBySearchQuery(query)).thenReturn(List.of(1L, 2L));
        when(concertRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(mockConcert1, mockConcert2));

        // When
        List<ConcertAutoCompleteResponse> result = concertService.getAutoComplete(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPrfnm()).isEqualTo("클래식 콘서트 1");
        assertThat(result.get(1).getPrfnm()).isEqualTo("클래식 콘서트 2");

        verify(concertRepository, times(1)).findConcertIdsBySearchQuery(query);
        verify(concertRepository, times(1)).findAllById(List.of(1L, 2L));
    }

    @Test
    @DisplayName("콘서트 좋아요 등록 및 취소")
    void testPostLikes() {
        // Given
        Long concertId = 1L;
        Long memberId = 10L;

        JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

        // Mock Member와 Concert 객체 생성
        Member mockMember = Member.builder()
            .id(memberId)
            .nickname("test_user")
            .build();

        Concert mockConcert = Concert.builder()
            .id(concertId)
            .prfnm("테스트 콘서트")
            .build();

        ConcertLike mockLike = ConcertLike.builder()
            .member(mockMember)
            .concert(mockConcert)
            .build();

        // Mock 설정
        when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
        when(jwtMemberDetailMock.getMemberId()).thenReturn(memberId);
        when(memberRepository.getById(memberId)).thenReturn(mockMember);
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(mockConcert));

        // 좋아요가 이미 있는 상태 Mock
        when(concertLikeRepository.findByMemberAndConcert(eq(mockMember), eq(mockConcert)))
            .thenReturn(Optional.of(mockLike));

        // When - 좋아요 취소
        String result = concertService.postLikes(concertId);

        // Then - 좋아요 취소 확인
        assertThat(result).isEqualTo("좋아요가 취소되었습니다.");
        verify(concertLikeRepository, times(1)).delete(mockLike);

        // 좋아요가 없는 상태 Mock
        when(concertLikeRepository.findByMemberAndConcert(eq(mockMember), eq(mockConcert)))
            .thenReturn(Optional.empty());

        // When - 좋아요 등록
        String result2 = concertService.postLikes(concertId);

        // Then - 좋아요 등록 확인
        assertThat(result2).isEqualTo("좋아요가 등록되었습니다.");
        verify(concertLikeRepository, times(1)).save(any(ConcertLike.class));
    }



    @Test
    @DisplayName("회원이 좋아요한 콘서트 조회")
    void testGetLikedConcert() {
        // Given
        Long memberId = 10L;
        Long concertId = 1L;

        JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

        Concert mockConcert = Concert.builder()
            .id(concertId)
            .prfnm("테스트 콘서트")
            .genrenm("Classical")
            .build();

        Category mockCategory = Category.builder()
            .id(1L)
            .category("웅장한")
            .imageUrl("image-url-1")
            .build();
        String query = "all";
        when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
        when(jwtMemberDetailMock.getMemberId()).thenReturn(memberId);
        when(concertLikeRepository.findConcertIdsByMemberId(memberId)).thenReturn(List.of(concertId));
        when(concertRepository.findConcertById(concertId)).thenReturn(mockConcert);
        when(concertRepository.findConcertIdsBySearchQuery(query)).thenReturn(List.of(concertId));
        when(concertCategoryRepository.findCategoryIdsByCategoryName(concertId)).thenReturn(List.of(1L));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(mockCategory));

        // When
        List<ConcertResponseV2> result = concertService.getLikedConcert("all", "all");

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrfnm()).isEqualTo("테스트 콘서트");
        assertThat(result.get(0).getCategories()).hasSize(1);
        assertThat(result.get(0).getCategories().get(0).getCategory()).isEqualTo("웅장한");

        verify(concertLikeRepository).findConcertIdsByMemberId(memberId);
        verify(concertRepository).findConcertById(concertId);
        verify(concertCategoryRepository).findCategoryIdsByCategoryName(concertId);
        verify(categoryRepository).findAllById(List.of(1L));

    }


    @Test
    @DisplayName("콘서트 검색 결과 조회")
    void testGetSearchConcert() {
        // Given
        String query = "클래식";
        String direction = "asc";

        Concert mockConcert1 = Concert.builder()
            .id(1L)
            .prfnm("클래식 콘서트 1")
            .prfpdfrom(LocalDate.of(2024, 1, 1))
            .prfpdto(LocalDate.of(2024, 12, 31))
            .build();

        Concert mockConcert2 = Concert.builder()
            .id(2L)
            .prfnm("클래식 콘서트 2")
            .prfpdfrom(LocalDate.of(2023, 1, 1))
            .prfpdto(LocalDate.of(2023, 12, 31))
            .build();

        Page<Concert> concertPage = new PageImpl<>(List.of(mockConcert1, mockConcert2), pageable, 2);

        when(concertRepository.findBySearchQuery(eq(query), any(Pageable.class)))
            .thenReturn(concertPage);

        when(concertCategoryRepository.findCategoryIdsByCategoryName(anyLong()))
            .thenReturn(List.of(1L));
        when(categoryRepository.findAllById(anyList()))
            .thenReturn(List.of(
                Category.builder().id(1L).category("웅장한").imageUrl("image-url-1").build()
            ));

        // When
        PageResponse<ConcertResponse> result = concertService.getSearchConcert(query, direction, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getListPageResponse()).hasSize(2);
        assertThat(result.getListPageResponse().get(0).getPrfnm()).isEqualTo("클래식 콘서트 1");
        assertThat(result.getListPageResponse().get(1).getPrfnm()).isEqualTo("클래식 콘서트 2");
        verify(concertRepository, times(1)).findBySearchQuery(eq(query), any(Pageable.class));
    }

    @Test
    @DisplayName("검색어 및 장르로 좋아요 콘서트 필터링")
    void testFilterConcertsByQueryAndGenre() {
        // Given
        List<Long> concertLikedIds = List.of(1L, 2L, 3L);
        String query = "클래식";
        String genre = "Classical";

        Concert mockConcert1 = Concert.builder()
            .id(1L)
            .prfnm("클래식 콘서트 1")
            .genrenm("Classical")
            .build();

        Concert mockConcert2 = Concert.builder()
            .id(2L)
            .prfnm("클래식 콘서트 2")
            .genrenm("Pop")
            .build();

        when(concertRepository.findConcertIdsBySearchQuery(query))
            .thenReturn(List.of(1L, 2L));
        when(concertRepository.findConcertById(1L))
            .thenReturn(mockConcert1);
        when(concertRepository.findConcertById(2L))
            .thenReturn(mockConcert2);

        // When
        List<Long> result = concertService.filterConcertsByQueryAndGenre(concertLikedIds, query, genre);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1); // Only mockConcert1 matches the genre "Classical"
        assertThat(result.get(0)).isEqualTo(1L);
        verify(concertRepository, times(1)).findConcertIdsBySearchQuery(query);
        verify(concertRepository, times(1)).findConcertById(1L);
        verify(concertRepository, times(1)).findConcertById(2L);
    }

}
