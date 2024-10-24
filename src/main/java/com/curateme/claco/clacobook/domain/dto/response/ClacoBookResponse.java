package com.curateme.claco.clacobook.domain.dto.response;

import com.curateme.claco.clacobook.domain.entity.ClacoBook;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.10.24
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.24   	   이 건        최초 생성
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClacoBookResponse {
	// claco book id
	@NotNull
	private Long id;
	// 제목
	@NotNull
	private String title;
	// 책 색깔
	@NotNull
	private String color;

	public static ClacoBookResponse fromEntity(ClacoBook clacoBook) {
		return new ClacoBookResponse(clacoBook.getId(), clacoBook.getTitle(), clacoBook.getColor());
	}

}
