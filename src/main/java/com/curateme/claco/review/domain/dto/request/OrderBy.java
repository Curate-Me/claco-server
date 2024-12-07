package com.curateme.claco.review.domain.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.11.04
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.04		   이 건		   최초 생성
 */
@Getter
@RequiredArgsConstructor
public enum OrderBy {
	// 별점 높은 순, 낮은 순, 최근순
	HIGH_RATE("star_rate desc"), LOW_RATE("star_rate asc"), RECENT("created_at desc");
	private final String orderBy;

}
