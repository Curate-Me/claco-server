package com.curateme.claco.concert.repository;

import com.curateme.claco.concert.domain.entity.Concert;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConcertRepositoryTest {

    @Autowired
    private ConcertRepository concertRepository;

    @BeforeEach
    void setUp() {
        concertRepository.saveAll(Arrays.asList(
            Concert.builder()
                .mt20id("C12345")
                .prfnm("웅장한 연극")
                .prfpdfrom(LocalDate.now().minusDays(10))
                .prfpdto(LocalDate.now().plusDays(5))
                .fcltynm("서울극장")
                .area("서울특별시")
                .genrenm("연극")
                .build(),
            Concert.builder()
                .mt20id("C12346")
                .prfnm("현대적인 뮤지컬")
                .prfpdfrom(LocalDate.now().minusDays(20))
                .prfpdto(LocalDate.now().plusDays(20))
                .fcltynm("대학로극장")
                .area("서울특별시")
                .genrenm("뮤지컬")
                .build(),
            Concert.builder()
                .mt20id("C12347")
                .prfnm("클래식 음악회")
                .prfpdfrom(LocalDate.now().minusDays(15))
                .prfpdto(LocalDate.now())
                .fcltynm("예술의전당")
                .area("서울특별시")
                .genrenm("음악회")
                .build()
        ));
    }

    @Test
    void testFindByIdIn() {
        // Given
        List<Long> ids = concertRepository.findAll().stream()
            .limit(3)
            .map(Concert::getId)
            .toList();
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<Concert> result = concertRepository.findByIdIn(ids, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().size()).isEqualTo(ids.size());
    }

    @Test
    void testFindConcertIdsByFilters() {
        // Given
        String area = "서울특별시";
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().plusDays(30);
        List<String> categories = Arrays.asList("웅장한", "현대적인");

        // When
        List<Long> concertIds = concertRepository.findConcertIdsByFilters(area, startDate, endDate, categories);

        // Then
        assertThat(concertIds).isNotNull();
    }

    @Test
    void testFindConcertIdsBySearchQuery() {
        // Given
        String query = "연극";

        // When
        List<Long> concertIds = concertRepository.findConcertIdsBySearchQuery(query);

        // Then
        assertThat(concertIds).isNotNull();
        assertThat(concertIds).isNotEmpty();
    }

    @Test
    void testFindConcertById() {
        // Given
        Long concertId = concertRepository.findAll().get(0).getId();

        // When
        Concert concert = concertRepository.findConcertById(concertId);

        // Then
        assertThat(concert).isNotNull();
        assertThat(concert.getId()).isEqualTo(concertId);
    }

    @Test
    void testFindBySearchQuery() {
        // Given
        String query = "뮤지컬";
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<Concert> result = concertRepository.findBySearchQuery(query, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getPrfnm()).contains("뮤지컬");
    }

    @Test
    void testFindConcertsByFilters() {
        // Given
        String area = "서울특별시";
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().plusDays(30);
        List<String> categories = Arrays.asList("웅장한", "현대적인");
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<Concert> result = concertRepository.findConcertsByFilters(area, startDate, endDate, categories, pageable);

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void testFindConcertsByGenreWithPagination() {
        // Given
        String genre = "음악회";
        PageRequest pageable = PageRequest.of(0, 10);

        // When
        Page<Concert> result = concertRepository.findConcertsByGenreWithPagination(genre, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).getGenrenm()).isEqualTo(genre);
    }
}
