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

import com.curateme.claco.review.domain.entity.TagCategory;
import com.curateme.claco.review.domain.vo.TagCategoryVO;
import com.curateme.claco.review.repository.TagCategoryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TagCategoryServiceTest {

	@Mock
	TagCategoryRepository tagCategoryRepository;

	@InjectMocks
	TagCategoryServiceImpl tagCategoryService;

	final String test1 = "test1";
	final String test2 = "test2";

	@Test
	@DisplayName("감상평 카테고리 리스트 불러오기")
	void readTagCategoryListTest() {
		// Given
		TagCategory category1 = TagCategory.builder()
			.id(1L)
			.name(test1)
			.build();

		TagCategory category2 = TagCategory.builder()
			.id(2L)
			.name(test2)
			.build();

		when(tagCategoryRepository.findAll()).thenReturn(List.of(category1, category2));

		// When
		List<TagCategoryVO> result = tagCategoryService.readTagCategoryList();

		// Then
		verify(tagCategoryRepository).findAll();

		assertThat(result.stream().filter(tagCategoryVO ->
				tagCategoryVO.getTagName().equals(test1) || tagCategoryVO.getTagName().equals(test2)
			)
			.toList())
			.hasSize(2);

	}

}