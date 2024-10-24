package com.curateme.claco.clacobook.service;

import java.util.List;

import com.curateme.claco.clacobook.domain.dto.request.UpdateClacoBookRequest;
import com.curateme.claco.clacobook.domain.dto.response.ClacoBookResponse;

/**
 * @author      : 이 건
 * @date        : 2024.10.24
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.24   	   이 건        최초 생성
 */
public interface ClacoBookService {

	/**
	 * ClacoBook 생성
	 * @return : 생성된 ClacoBook 정보
	 */
	ClacoBookResponse createClacoBook();

	/**
	 * 접근한 유저의 ClacoBook 정보들
	 * @return : 소유하고 있는 ClacoBook 정보들
	 */
	List<ClacoBookResponse> readClacoBooks();

	/**
	 * ClacoBook 수정
	 * @param updateRequest : 수정 요청
	 * @return : 수정한 ClacoBook 정보
	 */
	ClacoBookResponse updateClacoBook(UpdateClacoBookRequest updateRequest);

	/**
	 * ClacoBook soft delete
	 * @param bookId : 삭제하고자 하는 book id
	 */
	void deleteClacoBook(Long bookId);

}
