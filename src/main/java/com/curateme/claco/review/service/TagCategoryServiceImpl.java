package com.curateme.claco.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curateme.claco.review.domain.vo.TagCategoryVO;
import com.curateme.claco.review.repository.TagCategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TagCategoryServiceImpl implements TagCategoryService {

	private final TagCategoryRepository tagCategoryRepository;

	@Override
	public List<TagCategoryVO> readTagCategoryList() {
		return tagCategoryRepository.findAll().stream()
			.map(TagCategoryVO::fromEntity)
			.toList();
	}
}
