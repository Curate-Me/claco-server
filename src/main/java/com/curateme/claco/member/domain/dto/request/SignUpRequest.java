package com.curateme.claco.member.domain.dto.request;

import java.util.List;

import com.curateme.claco.member.domain.entity.Gender;
import com.curateme.claco.preference.domain.vo.CategoryPreferenceVO;
import com.curateme.claco.preference.domain.vo.RegionPreferenceVO;
import com.curateme.claco.preference.domain.vo.TypePreferenceVO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {

	@NotNull
	@Schema(description = "닉네임", example = "클라코 사용자")
	private String nickname;
	@NotNull
	@Schema(description = "성별, MALE 아니면 FEMALE로 입력", example = "FEMALE")
	private Gender gender;
	@NotNull
	@Schema(description = "나이", example = "15")
	private Integer age;
	@NotNull
	@Schema(description = "선호 최소 가격", example = "1000")
	private Integer minPrice;
	@NotNull
	@Schema(description = "선호 최대 가격", example = "1000000")
	private Integer maxPrice;
	@Schema(description = "지역 선호도 선택한 문자열 리스트")
	private List<RegionPreferenceVO> regionPreferences;
	@Schema(description = "공연 유형 선택한 문자열 리스트")
	private List<TypePreferenceVO> typePreferences;
	@Schema(description = "공연 성격 선택한 문자열 리스트")
	private List<CategoryPreferenceVO> categoryPreferences;

}
