package com.curateme.claco.concert.repository;

import com.curateme.claco.concert.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

}
