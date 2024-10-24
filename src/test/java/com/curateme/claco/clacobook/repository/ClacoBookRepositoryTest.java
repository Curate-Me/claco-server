package com.curateme.claco.clacobook.repository;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
class ClacoBookRepositoryTest {

	@Autowired
	EntityManager entityManager;
	@Autowired
	ClacoBookRepository clacoBookRepository;

	@Test
	void findClacoBookById() {
		// Given
		String testString = "test";

		Member testMember = Member.builder()
			.email("test@test.com")
			.role(Role.MEMBER)
			.socialId(1L)
			.build();

		entityManager.persist(testMember);

		ClacoBook testBook = ClacoBook.builder()
			.title(testString)
			.color(testString)
			.member(testMember)
			.build();

		entityManager.persist(testBook);

		Long testId = testBook.getId();

		// When
		Optional<ClacoBook> result = clacoBookRepository.findClacoBookById(testId);

		// Then
		assertThat(result.isPresent()).isTrue();
		assertThat(result.get().getMember()).isEqualTo(testMember);
		assertThat(result.get().getTitle()).isEqualTo(testString);

	}
}