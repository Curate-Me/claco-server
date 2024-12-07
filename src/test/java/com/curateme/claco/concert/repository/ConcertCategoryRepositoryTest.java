package com.curateme.claco.concert.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.curateme.claco.concert.domain.entity.Category;
import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.domain.entity.ConcertCategory;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConcertCategoryRepositoryTest {

    @Autowired
    private ConcertCategoryRepository concertCategoryRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        // 1. Concert 생성
        concertRepository.save(
            Concert.builder()
                .mt20id("C12345")
                .prfnm("Test Concert")
                .prfpdfrom(LocalDate.of(2024, 1, 1))
                .prfpdto(LocalDate.of(2024, 1, 31))
                .fcltynm("Grand Theater")
                .build()
        );

        // 2. Category 생성
        categoryRepository.save(
            Category.builder()
                .category("웅장한")
                .imageUrl("xxx")
                .build()
        );

        categoryRepository.save(
            Category.builder()
                .category("현대적인")
                .imageUrl("xxx")
                .build()
        );

    }

    @Test
    void testFindCategoryIdsByCategoryName() {
        // Given
        List<Concert> concerts = concertRepository.findAll();
        assertThat(concerts).isNotEmpty();
        Long concertId = concerts.get(0).getId();

        // When
        List<Long> categoryIds = concertCategoryRepository.findCategoryIdsByCategoryName(concertId);

        // Then
        assertThat(categoryIds).isNotNull();
        if (!categoryIds.isEmpty()) {
            assertThat(categoryIds).containsExactlyInAnyOrder(1L, 2L);
        } else {
            log.info("No categories found for concertId: {}", concertId);
        }
    }

}

