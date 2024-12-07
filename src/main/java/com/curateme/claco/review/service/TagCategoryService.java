package com.curateme.claco.review.service;

import java.util.List;

import com.curateme.claco.review.domain.vo.TagCategoryVO;

/**
 * @author      : 이 건
 * @date        : 2024.11.03
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.03   	   이 건        최초 생성
 */
public interface TagCategoryService {

	/**
	 * 감상평 카테고리 리스트 읽어오기
	 * @return : 감상평 카테고리 이름 및 id 반환
	 */
	List<TagCategoryVO> readTagCategoryList();

}
