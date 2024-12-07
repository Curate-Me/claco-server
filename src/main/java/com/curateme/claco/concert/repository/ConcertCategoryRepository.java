package com.curateme.claco.concert.repository;

import com.curateme.claco.concert.domain.entity.ConcertCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConcertCategoryRepository extends JpaRepository<ConcertCategory,Long> {
    @Query("SELECT cc.category.id FROM ConcertCategory cc WHERE cc.concert.id = :concertId")
    List<Long> findCategoryIdsByCategoryName(@Param("concertId") Long concertId);

    @Query("SELECT cc.category.category FROM ConcertCategory cc WHERE cc.concert.id = :concertId")
    List<String> findCategoryNamesByConcertId(@Param("concertId") Long concertId);
}

