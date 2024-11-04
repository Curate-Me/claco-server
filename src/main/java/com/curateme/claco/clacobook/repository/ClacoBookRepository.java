package com.curateme.claco.clacobook.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.curateme.claco.clacobook.domain.entity.ClacoBook;

/**
 * @author      : 이 건
 * @date        : 2024.10.24
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.24   	   이 건        최초 생성
 * 2024.11.04   	   이 건        티켓 리뷰 조인 조회 기능 추가
 */
public interface ClacoBookRepository extends JpaRepository<ClacoBook, Long> {

	/**
	 * member 엔티티와 함께 claco book id로 찾는 메서드
	 * @param id : 찾고자 하는 claco book id
	 * @return : Optional ClacoBook
	 */
	@EntityGraph(attributePaths = {"member"})
	Optional<ClacoBook> findClacoBookById(Long id);

	/**
	 * TicketReview 엔티티 조인 조회 메서드
	 * @param id : 찾고자 하는 ClacoBook id
	 * @return : Optional ClacoBook
	 */
	@EntityGraph(attributePaths = {"ticketReviews"})
	Optional<ClacoBook> findByIdIs(Long id);

}
