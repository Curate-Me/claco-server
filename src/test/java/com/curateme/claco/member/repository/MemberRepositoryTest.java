package com.curateme.claco.member.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;
import com.curateme.claco.preference.domain.entity.Preference;
import com.curateme.claco.preference.domain.entity.RegionPreference;
import com.curateme.claco.preference.domain.entity.TypePreference;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

/**
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 * 2024.11.05   	   이 건        추가된 메서드 테스트 추가
 */
@Slf4j
@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;
	@Autowired
	EntityManager entityManager;

	private final Role testRole = Role.MEMBER;
	private final Long testLong = 1L;
	private final String testString = "test";

	@Test
	@DisplayName("소셜 아이디로 멤버 찾기")
	void findMemberBySocialId() {
		// Given
		Member testMember = Member.builder()
			.email("test@test.com")
			.nickname(testString)
			.role(testRole)
			.socialId(testLong)
			.profileImage(testString)
			.build();

		entityManager.persist(testMember);

		// When
		Optional<Member> assertMember = memberRepository.findMemberBySocialId(testLong);

		// Then
		assertThat(assertMember.isPresent()).isTrue();
		assertThat(assertMember.get()).isEqualTo(testMember);
		assertThat(assertMember.get().getSocialId()).isEqualTo(testMember.getSocialId());

	}

	@Test
	@DisplayName("닉네임으로 멤버 찾기")
	void findMemberByNickname() {
		// Given
		Member testMember = Member.builder()
			.email("test@test.com")
			.nickname(testString)
			.role(testRole)
			.socialId(testLong)
			.profileImage(testString)
			.build();

		entityManager.persist(testMember);

		// When
		Optional<Member> assertMember = memberRepository.findMemberByNickname(testString);

		// Then
		assertThat(assertMember.isPresent()).isTrue();
		assertThat(assertMember.get()).isEqualTo(testMember);
		assertThat(assertMember.get().getNickname()).isEqualTo(testMember.getNickname());

	}

	@Test
	@DisplayName("아이디로 클라코북과 함께 멤버 찾기")
	void findMemberByNicknameWithClacoBook() {
		// Given
		Member testMember = Member.builder()
			.email("test@test.com")
			.nickname(testString)
			.role(testRole)
			.socialId(testLong)
			.profileImage(testString)
			.build();

		entityManager.persist(testMember);

		Long curId = testMember.getId();

		ClacoBook clacoBook1 = ClacoBook.builder()
			.title(testString)
			.color(testString)
			.member(testMember)
			.build();

		ClacoBook clacoBook2 = ClacoBook.builder()
			.title(testString)
			.color(testString)
			.member(testMember)
			.build();

		entityManager.persist(clacoBook1);
		entityManager.persist(clacoBook2);

		testMember.addClacoBook(clacoBook1);
		testMember.addClacoBook(clacoBook2);

		// When
		Optional<Member> result = memberRepository.findMemberByIdWithClacoBook(curId);

		// Then
		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getClacoBooks().size()).isEqualTo(2);

	}

	@Test
	@DisplayName("취향 정보 함께 조회")
	void findMemberWithPreference() {
		// Given
		Member testMember = Member.builder()
			.email("test@test.com")
			.nickname(testString)
			.role(testRole)
			.socialId(testLong)
			.profileImage(testString)
			.build();
		entityManager.persist(testMember);

		Preference preference = Preference.builder()
			.member(testMember)
			.build();
		entityManager.persist(preference);
		testMember.updatePreference(preference);

		RegionPreference regionPreference = RegionPreference.builder()
			.preference(preference)
			.regionName(testString)
			.build();
		entityManager.persist(regionPreference);
		preference.addRegionPreference(regionPreference);
		TypePreference typePreference = TypePreference.builder()
			.preference(preference)
			.typeContent(testString)
			.build();
		entityManager.persist(typePreference);
		preference.addTypeReference(typePreference);

		// When
		Optional<Member> result = memberRepository.findMemberByIdIs(testMember.getId());

		// Then
		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getPreference()).isEqualTo(preference);
		assertThat(result.get().getPreference().getRegionPreferences()).contains(regionPreference);
		assertThat(result.get().getPreference().getTypePreferences()).contains(typePreference);

	}


}