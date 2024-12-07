package com.curateme.claco.review.domain.vo;

import com.curateme.claco.review.domain.entity.TagCategory;

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
public class TagCategoryVO {

	// id
	@Schema(description = "공연 성격 Id", example = "1")
	private Long tagCategoryId;
	// 태그 이름
	@Schema(description = "공연 성격 이름", example = "웅장한")
	private String tagName;
	@Schema(description = "공연 성격 아이콘 이미지", example = "https://claco.com")
	private String iconUrl;

	public static TagCategoryVO fromEntity(TagCategory tagCategory) {
		return new TagCategoryVO(tagCategory.getId(), tagCategory.getName(), tagCategory.getImageUrl());
	}

}
