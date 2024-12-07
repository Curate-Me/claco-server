package com.curateme.claco.preference.domain.vo;

import com.curateme.claco.preference.domain.entity.TypePreference;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TypePreferenceVO {

	@Schema(description = "수정하고자 하는 유형 내용", example = "깊이 있는 클래식 공연이 좋아요.")
	private String preferenceType;

	public static TypePreferenceVO fromEntity(TypePreference typePreference) {
		return new TypePreferenceVO(typePreference.getTypeContent());
	}

}
