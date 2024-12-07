package com.curateme.claco.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.curateme.claco.review.domain.entity.TagCategory;

public interface TagCategoryRepository extends JpaRepository<TagCategory, Long> {
}
