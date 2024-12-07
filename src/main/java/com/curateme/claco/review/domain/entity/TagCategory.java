package com.curateme.claco.review.domain.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.curateme.claco.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * 2024.11.05		   이 건		   이미지 url 추가
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE tag_category SET active_status = 'DELETED' WHERE tag_category_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
public class TagCategory extends BaseEntity {

	@Id @Column(name = "tag_category_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// 태그명
	private String name;
	// 이미지 Url
	private String imageUrl;

}
