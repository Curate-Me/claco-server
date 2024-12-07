package com.curateme.claco.review.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curateme.claco.review.domain.vo.PlaceCategoryVO;
import com.curateme.claco.review.repository.PlaceCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PlaceCategoryServiceImpl implements PlaceCategoryService{

	private final PlaceCategoryRepository placeCategoryRepository;

	@Override
	public List<PlaceCategoryVO> readPlaceCategoryList() {

		return placeCategoryRepository.findAll().stream()
			.map(PlaceCategoryVO::fromEntity)
			.toList();
	}
}
