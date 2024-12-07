package com.curateme.claco.preference.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryPreferenceVO {

	@Schema(description = "선택한 공연 성격 정보", example = "웅장한")
	private String preferenceCategory;

}
