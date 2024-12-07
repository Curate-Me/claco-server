package com.curateme.claco.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.curateme.claco.review.domain.entity.PlaceCategory;
import com.curateme.claco.review.domain.vo.PlaceCategoryVO;
import com.curateme.claco.review.repository.PlaceCategoryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PlaceCategoryServiceTest {

	@Mock
	PlaceCategoryRepository placeCategoryRepository;
	@InjectMocks
	PlaceCategoryServiceImpl placeCategoryService;

	@Test
	@DisplayName("장소평 카테고리 가져오기")
	void readPlaceCategoryList() {
		// Given
		PlaceCategory category1 = PlaceCategory.builder()
			.id(1L)
			.name("test1")
			.build();

		PlaceCategory category2 = PlaceCategory.builder()
			.id(2L)
			.name("test2")
			.build();

		when(placeCategoryRepository.findAll()).thenReturn(List.of(category1, category2));

		// When
		List<PlaceCategoryVO> assertResult = placeCategoryService.readPlaceCategoryList();

		// Then
		verify(placeCategoryRepository).findAll();

		assertThat(assertResult.size()).isEqualTo(2);

	}


}