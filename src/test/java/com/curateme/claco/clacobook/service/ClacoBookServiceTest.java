package com.curateme.claco.clacobook.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.curateme.claco.authentication.domain.JwtMemberDetail;
import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.clacobook.domain.dto.request.UpdateClacoBookRequest;
import com.curateme.claco.clacobook.domain.dto.response.ClacoBookResponse;
import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.clacobook.repository.ClacoBookRepository;
import com.curateme.claco.global.entity.ActiveStatus;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ClacoBookServiceTest {

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private ClacoBookRepository clacoBookRepository;
	@Mock
	private SecurityContextUtil securityContextUtil;
	@InjectMocks
	private ClacoBookService clacoBookService;

	private final Long testId = 1L;
	private final String testString = "test";

	@Test
	void createClacoBook() {
		// Given

		JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);
		Member testMember = Member.builder()
			.id(testId)
			.email("test@test.com")
			.nickname("test")
			.role(Role.MEMBER)
			.socialId(testId)
			.build();

		ClacoBook testBook = ClacoBook.builder()
			.member(testMember)
			.id(testId)
			.title(testString)
			.color(testString)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
		when(jwtMemberDetailMock.getMemberId()).thenReturn(testId);
		when(memberRepository.findById(testId)).thenReturn(Optional.of(testMember));
		when(clacoBookRepository.save(any(ClacoBook.class))).thenReturn(testBook);

		// When
		ClacoBookResponse result = clacoBookService.createClacoBook();

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(jwtMemberDetailMock).getMemberId();
		verify(memberRepository).findById(testId);

		String bookTitle = "test님의 이야기";
		String bookColor = "#8F9AF8";

		assertThat(result.getId()).isEqualTo(testId);
		assertThat(result.getTitle()).isEqualTo(bookTitle);
		assertThat(result.getColor()).isEqualTo(bookColor);

	}

	@Test
	@DisplayName("사용자가 접근했을 때")
	void readClacoBooks() {
		// Given
		Member testMember = Member.builder()
			.id(testId)
			.email("test@test.com")
			.nickname("test")
			.role(Role.MEMBER)
			.socialId(testId)
			.build();

		ClacoBook testBook1 = ClacoBook.builder()
			.member(testMember)
			.id(testId)
			.title(testString)
			.color(testString)
			.build();

		ClacoBook testBook2 = ClacoBook.builder()
			.member(testMember)
			.id(testId)
			.title(testString)
			.color(testString)
			.build();

		testMember.addClacoBook(testBook1);
		testMember.addClacoBook(testBook2);

		JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

		when(jwtMemberDetailMock.getMemberId()).thenReturn(testId);
		when(memberRepository.findMemberByIdWithClacoBook(testId)).thenReturn(Optional.of(testMember));

		// When
		List<ClacoBookResponse> result = clacoBookService.readClacoBooks();

		// Then
		verify(jwtMemberDetailMock).getMemberId();
		verify(memberRepository).findMemberByIdWithClacoBook(testId);

		assertThat(result.size()).isEqualTo(2);

	}

	@Test
	void updateClacoBook() {
		// Given
		Member testMember = Member.builder()
			.id(testId)
			.email("test@test.com")
			.nickname("test")
			.role(Role.MEMBER)
			.socialId(testId)
			.build();

		ClacoBook testBook1 = ClacoBook.builder()
			.member(testMember)
			.id(testId)
			.title(testString)
			.color(testString)
			.build();

		testMember.addClacoBook(testBook1);

		JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

		when(jwtMemberDetailMock.getMemberId()).thenReturn(testId);
		when(clacoBookRepository.findClacoBookById(testId)).thenReturn(Optional.of(testBook1));

		String edit = "new";

		UpdateClacoBookRequest request = UpdateClacoBookRequest.builder()
			.id(testId)
			.title(edit)
			.color(edit)
			.build();

		// When
		ClacoBookResponse result = clacoBookService.updateClacoBook(request);

		// Then
		verify(jwtMemberDetailMock).getMemberId();
		verify(clacoBookRepository).findClacoBookById(testId);

		assertThat(result.getId()).isEqualTo(testId);
		assertThat(result.getTitle()).isEqualTo(edit);
		assertThat(result.getColor()).isEqualTo(edit);

	}

	@Test
	void deleteClacoBook() {
		// Given
		Member testMember = Member.builder()
			.id(testId)
			.email("test@test.com")
			.nickname("test")
			.role(Role.MEMBER)
			.socialId(testId)
			.build();

		ClacoBook testBook1 = ClacoBook.builder()
			.member(testMember)
			.id(testId)
			.title(testString)
			.color(testString)
			.build();

		testMember.addClacoBook(testBook1);

		JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

		when(jwtMemberDetailMock.getMemberId()).thenReturn(testId);
		when(clacoBookRepository.findClacoBookById(testId)).thenReturn(Optional.of(testBook1));

		// When
		clacoBookService.deleteClacoBook(testId);

		// Then
		verify(jwtMemberDetailMock).getMemberId();
		verify(clacoBookRepository).findClacoBookById(testId);

		assertThat(testBook1.getActiveStatus()).isEqualTo(ActiveStatus.DELETED);

	}
}