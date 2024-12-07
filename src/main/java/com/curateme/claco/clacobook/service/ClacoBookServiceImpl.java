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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ClacoBookServiceImpl {

	private final ClacoBookRepository clacoBookRepository;
	private final MemberRepository memberRepository;
	private final SecurityContextUtil securityContextUtil;

	public ClacoBookResponse createClacoBook(UpdateClacoBookRequest request) {
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
			.title(request.getTitle() != null ? request.getTitle() : member.getNickname() + "님의 이야기")
			.color(request.getColor() != null ? request.getColor() : "#5B120B")
			.build();

		return ClacoBookResponse.fromEntity(clacoBookRepository.save(clacoBook));
	}

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
