package com.curateme.claco.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.curateme.claco.review.domain.entity.ReviewTag;

public interface ReviewTagRepository extends JpaRepository<ReviewTag, Long> {
}
