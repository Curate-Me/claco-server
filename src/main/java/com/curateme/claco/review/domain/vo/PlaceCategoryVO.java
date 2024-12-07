package com.curateme.claco.review.domain.vo;

import com.curateme.claco.review.domain.entity.PlaceCategory;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.11.03
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.03		   이 건		   최초 생성
 * 2024.11.05		   이 건		   Swagger 적용
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceCategoryVO {

	// id
	@Schema(description = "장소평 카테고리 id", example = "1")
	private Long placeCategoryId;
	// 장소평 태그 이름
	@Schema(description = "장소평 카테고리 이름", example = "깨끗해요.")
	private String categoryName;

	public static PlaceCategoryVO fromEntity(PlaceCategory placeCategory) {
		return new PlaceCategoryVO(placeCategory.getId(), placeCategory.getName());
	}

}
