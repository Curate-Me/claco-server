package com.curateme.claco.clacobook.domain.dto.request;

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
 * 2024.11.05   	   이 건        Swagger 적용 및 제약 조건 해제
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateClacoBookRequest {
	// id
	@Schema(description = "클라코북 id, create 시 null로 줘도 됨", example = "0")
	private Long id;
	// 제목
	@Schema(description = "클라코북 이름", example = "발레 공연 아카이빙")
	private String title;
	// 책 컬러
	@Schema(description = "클라코북 색상", example = "#5B5B5B")
	private String color;


}
