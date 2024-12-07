package com.curateme.claco.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.curateme.claco.member.domain.entity.Member;

/**
 * @packageName : com.curateme.claco.member.repository
 * @fileName    : MemberRepository.java
 * @author      : 이 건
 * @date        : 2024.10.17
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.17   	   이 건        최초 생성
 * 2024.10.18   	   이 건        nickname 메서드 추가 -> id 로 변경
 * 2024.11.05   	   이 건        preference entity graph 메서드 추가
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

	/**
	 * 소셜 id 로 Member 찾는 메서드
	 * @param socialId : OAuth 가입 시 발급된 socialId
	 * @return : Optional Member
	 */
	Optional<Member> findMemberBySocialId(Long socialId);

	/**
	 * 닉네임으로 Member 찾는 메서드
	 * @param nickname : 찾고자 하는 닉네임
	 * @return : Optional Member
	 */
	Optional<Member> findMemberByNickname(String nickname);

	/**
	 * 닉네임으로 Member 를 클라코 북과 함께 찾는 메서드
	 * @param id : 찾고자 하는 유저의 id
	 * @return : Optional Member
	 */
	@EntityGraph(attributePaths = {"clacoBooks"})
	@Query("select m from Member m where m.id=:id")
	Optional<Member> findMemberByIdWithClacoBook(@Param("id") Long id);

	/**
	 * preference, typePreferences, regionPreferences 조인하여 조회
	 * @param id : 찾고자 하는 회원의 id
	 * @return : 취향 정보를 모두 포함한 회원 정보
	 */
	@EntityGraph(attributePaths = {"preference", "preference.typePreferences", "preference.regionPreferences"})
	Optional<Member> findMemberByIdIs(Long id);

}
