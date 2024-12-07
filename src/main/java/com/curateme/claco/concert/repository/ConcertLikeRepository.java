package com.curateme.claco.concert.repository;

import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.domain.entity.ConcertLike;
import com.curateme.claco.member.domain.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConcertLikeRepository extends JpaRepository<ConcertLike,Long> {

    @Query("SELECT cl FROM ConcertLike cl WHERE cl.member = :member AND cl.concert = :concert")
    Optional<ConcertLike> findByMemberAndConcert(@Param("member") Member member, @Param("concert") Concert concert);

    @Query("SELECT CASE WHEN COUNT(cl) > 0 THEN true ELSE false END FROM ConcertLike cl WHERE cl.concert.id = :concertId")
    boolean existsByConcertId(@Param("concertId") Long concertId);

    @Query("SELECT cl.concert.id FROM ConcertLike cl WHERE cl.member.id = :userId ORDER BY cl.createdAt DESC")
    Page<Long> findMostRecentLikedConcert(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT cl.concert.id FROM ConcertLike cl WHERE cl.member.id = :userId")
    List<Long> findConcertIdsByMemberId(@Param("userId") Long userId);

    @Query("SELECT cl.concert.id " + "FROM ConcertLike cl " + "GROUP BY cl.concert.id " + "ORDER BY COUNT(cl) DESC")
    List<Long> findTopConcertIdsByLikeCount(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(cl) > 0 THEN true ELSE false END " +
        "FROM ConcertLike cl " +
        "WHERE cl.concert.id = :concertId AND cl.member.id = :memberId")
    boolean existsByConcertIdAndMemberId(@Param("concertId") Long concertId, @Param("memberId") Long memberId);

}

