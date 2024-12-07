package com.curateme.claco.preference.domain.dto.request;

import java.util.List;

import com.curateme.claco.member.domain.entity.Gender;
import com.curateme.claco.preference.domain.vo.RegionPreferenceVO;
import com.curateme.claco.preference.domain.vo.TypePreferenceVO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : 이 건
 * @date : 2024.11.05
 * @author devkeon(devkeon123 @ gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.05   	   이 건        최초 생성
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferenceUpdateRequest {

	@Schema(description = "수정하고자 하는 성별, 수정 없으면 null", example = "MALE / FEMALE")
	private Gender gender;
	@Schema(description = "수정하고자 하는 나이, 수정 없으면 null", example = "26")
	private Integer age;
	@Schema(description = "수정하고자 하는 최소 가격, 수정 없으면 null", example = "1000")
	private Integer minPrice;
	@Schema(description = "수정하고자 하는 최대 가격, 수정 없으면 null", example = "1000000")
	private Integer maxPrice;
	@Schema(description = "수정하고자 하는 지역 선호도, 새롭게 추가된 데이터 + 유지된 데이터")
	private List<RegionPreferenceVO> regionPreferences;
	@Schema(description = "수정하고자 하는 공연 유형, 새롭게 추가된 데이터 + 유지된 데이터")
	private List<TypePreferenceVO> typePreferences;

}
