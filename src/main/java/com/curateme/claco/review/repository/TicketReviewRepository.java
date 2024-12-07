package com.curateme.claco.review.repository;

import com.curateme.claco.review.domain.dto.response.TicketReviewSummaryResponse;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.review.domain.entity.TicketReview;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author      : 이 건
 * @date        : 2024.10.29
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.29   	   이 건        최초 생성
 * 2024.11.04   	   이 건        조회 및 카운팅 메서드 추가
 */
public interface TicketReviewRepository extends JpaRepository<TicketReview, Long> {

	/**
	 * 콘서트 정보 제외 호출
	 * @param id : 조회하고자 하는 TicketReview id
	 * @return : TickerReview optional
	 */
	@EntityGraph(attributePaths = {"placeReviews", "placeReviews.placeCategory", "reviewImages", "reviewTags",
		"reviewTags.tagCategory", "member"})
	Optional<TicketReview> findTicketReviewById(Long id);

	/**
	 * 모든 정보 조인 후 호출
	 * @param id : 조회하고자 하는 티켓리뷰 id
	 * @return : TickerReview optional
	 */
	@EntityGraph(attributePaths = {"placeReviews", "placeReviews.placeCategory", "reviewImages", "reviewTags",
		"reviewTags.tagCategory", "member", "concert"})
	Optional<TicketReview> findTicketReviewByIdIs(Long id);

	/**
	 * 별점 높은 순 정렬, 페이징 조회
	 * @param concert : 조회하고자 하는 공연 엔티티
	 * @param pageable : 페이징
	 * @return : TicketReview 객체 리스트 (페이징)
	 */
	@EntityGraph(attributePaths = {"placeReviews", "placeReviews.placeCategory", "reviewImages", "member"})
	Page<TicketReview> findAllByConcertOrderByStarRateDescIdDesc(Concert concert, Pageable pageable);

	/**
	 * 별점 낮은 순 정렬, 페이징 조회
	 * @param concert : 조회하고자 하는 공연 엔티티
	 * @param pageable : 페이징
	 * @return : TicketReview 객체 리스트 (페이징)
	 */
	@EntityGraph(attributePaths = {"placeReviews", "placeReviews.placeCategory", "reviewImages", "member"})
	Page<TicketReview> findAllByConcertOrderByStarRateAscIdDesc(Concert concert, Pageable pageable);

	/**
	 * 최신순 정렬, 페이징 조회
	 * @param concert : 조회하고자 하는 공연 엔티티
	 * @param pageable : 페이징
	 * @return : TicketReview 객체 리스트 (페이징)
	 */
	@EntityGraph(attributePaths = {"placeReviews", "placeReviews.placeCategory", "reviewImages", "member"})
	Page<TicketReview> findAllByConcertOrderByIdDesc(Concert concert, Pageable pageable);

	/**
	 * 공연의 총 리뷰 개수 조회 메서드
	 * @param concert : 조회하고자 하는 공연
	 * @return : 총 리뷰 개수
	 */
	Integer countTicketReviewByConcert(Concert concert);

	List<TicketReview> findByClacoBook(ClacoBook clacoBook);

	@Query("SELECT new com.curateme.claco.review.domain.dto.response.TicketReviewSummaryResponse(tr.member.nickname, tr.concert.prfnm , tr.concert.id, tr.createdAt, tr.content) " +
		"FROM TicketReview tr WHERE tr.id = :id")
	TicketReviewSummaryResponse findSummaryById(@Param("id") Long id);

	@Query("SELECT tr.id FROM TicketReview tr WHERE tr.concert.id = :concertId")
	List<Long> findByConcertId(@Param("concertId") Long concertId);
}
