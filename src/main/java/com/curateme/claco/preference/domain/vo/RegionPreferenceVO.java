package com.curateme.claco.preference.domain.vo;

import com.curateme.claco.preference.domain.entity.RegionPreference;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegionPreferenceVO {

	@Schema(description = "수정하고자 하는 지역 이름", example = "서울")
	private String preferenceRegion;

	public static RegionPreferenceVO fromEntity(RegionPreference regionPreference) {
		return new RegionPreferenceVO(regionPreference.getRegionName());
	}

}
