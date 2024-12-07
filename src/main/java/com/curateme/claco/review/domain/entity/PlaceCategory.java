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
 * @date        : 2024.10.28
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.28		   이 건		   최초 생성
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE place_category SET active_status = 'DELETED' WHERE place_category_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
public class PlaceCategory extends BaseEntity {

	@Id @Column(name = "place_category_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// 카테고리 이름
	private String name;

}
