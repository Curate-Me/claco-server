package com.curateme.claco.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.curateme.claco.review.domain.entity.PlaceCategory;

/**
 * @author      : 이 건
 * @date        : 2024.10.29
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.29   	   이 건        최초 생성
 */
public interface PlaceCategoryRepository extends JpaRepository<PlaceCategory, Long> {
}
