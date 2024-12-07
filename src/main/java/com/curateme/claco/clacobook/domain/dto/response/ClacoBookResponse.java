package com.curateme.claco.clacobook.domain.dto.response;

import com.curateme.claco.clacobook.domain.entity.ClacoBook;

import io.swagger.v3.oas.annotations.media.Schema;
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
 * 2024.11.05   	   이 건        Swagger 적용
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClacoBookResponse {
	// claco book id
	@NotNull
	@Schema(description = "클라코 북 id", example = "1")
	private Long id;
	// 제목
	@NotNull
	@Schema(description = "클라코 북 제목", example = "OO님의 이야기")
	private String title;
	// 책 색깔
	@NotNull
	@Schema(description = "클라코북 색상", example = "#000000")
	private String color;

	public static ClacoBookResponse fromEntity(ClacoBook clacoBook) {
		return new ClacoBookResponse(clacoBook.getId(), clacoBook.getTitle(), clacoBook.getColor());
	}

}
