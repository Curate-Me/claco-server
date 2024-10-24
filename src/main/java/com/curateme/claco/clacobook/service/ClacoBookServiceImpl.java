package com.curateme.claco.clacobook.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.clacobook.domain.dto.request.UpdateClacoBookRequest;
import com.curateme.claco.clacobook.domain.dto.response.ClacoBookResponse;
import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.clacobook.repository.ClacoBookRepository;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author      : 이 건
 * @date        : 2024.10.24
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.24   	   이 건        최초 생성
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClacoBookServiceImpl implements ClacoBookService {

	private final ClacoBookRepository clacoBookRepository;
	private final MemberRepository memberRepository;
	private final SecurityContextUtil securityContextUtil;

	@Override
	public ClacoBookResponse createClacoBook() {
		// 접근 사용자의 ClacoBook 생성
		Member member = memberRepository.findMemberByIdWithClacoBook(
				securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		if (member.getClacoBooks().size() >= 5) {
			throw new BusinessException(ApiStatus.CLACO_BOOK_CREATION_LIMIT);
		}

		ClacoBook clacoBook = ClacoBook.builder()
			.member(member)
			.title(member.getNickname() + "님의 이야기")
			.color("#8F9AF8")
			.build();

		return ClacoBookResponse.fromEntity(clacoBookRepository.save(clacoBook));
	}

	@Override
	public List<ClacoBookResponse> readClacoBooks() {
		// 접근 사용자의 ClacoBook 조회
		Member member = memberRepository.findMemberByIdWithClacoBook(
				securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		return member.getClacoBooks().stream()
			.map(ClacoBookResponse::fromEntity)
			.toList();
	}

	@Override
	public ClacoBookResponse updateClacoBook(UpdateClacoBookRequest updateRequest) {
		// 소유주 검사
		Long contextMemberId = securityContextUtil.getContextMemberInfo().getMemberId();

		ClacoBook clacoBook = clacoBookRepository.findClacoBookById(updateRequest.getId()).stream()
			.filter(clacoBook1 -> clacoBook1.getMember().getId().equals(contextMemberId))
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.CLACO_BOOK_NOT_FOUND));

		// 수정
		clacoBook.updateTitle(updateRequest.getTitle() == null ? clacoBook.getTitle() : updateRequest.getTitle());
		clacoBook.updateColor(updateRequest.getColor() == null ? clacoBook.getColor() : updateRequest.getColor());

		return ClacoBookResponse.fromEntity(clacoBook);
	}

	@Override
	public void deleteClacoBook(Long bookId) {
		// 소유주 검사
		Long contextMemberId = securityContextUtil.getContextMemberInfo().getMemberId();

		ClacoBook deleteClacoBook = clacoBookRepository.findClacoBookById(bookId).stream()
			.filter(clacoBook -> clacoBook.getMember().getId().equals(contextMemberId))
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.CLACO_BOOK_NOT_FOUND));
		// 삭제
		clacoBookRepository.delete(deleteClacoBook);
	}
}
