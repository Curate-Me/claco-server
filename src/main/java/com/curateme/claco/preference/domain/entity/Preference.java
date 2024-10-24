package com.curateme.claco.preference.domain.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.curateme.claco.global.entity.BaseEntity;
import com.curateme.claco.member.domain.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.10.22
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.22   	   이 건        최초 생성
 * 2024.10.24   	   이 건        soft delete 조건 추가
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE preference SET active_status = 'DELETED' WHERE preference_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
public class Preference extends BaseEntity {

	// auto_increment 사용 id
	@Id
	@Column(name = "preference_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Member 일대일 양방향 매핑 (대상 테이블)
	@OneToOne(mappedBy = "preference", fetch = FetchType.LAZY)
	private Member member;
	// 다대일 매핑
	@Builder.Default
	@OneToMany(mappedBy = "preference", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TypePreference> typePreferences= new ArrayList<>();
	// 다대일 매핑
	@Builder.Default
	@OneToMany(mappedBy = "preference", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RegionPreference> regionPreferences = new ArrayList<>();

	// 카테고리 기반 취향
	private String preference1;
	private String preference2;
	private String preference3;
	private String preference4;
	private String preference5;
	// 선호 가격대
	private Integer minPrice;
	private Integer maxPrice;

	// 연관관계 편의 메서드
	public void addTypeReference(TypePreference typePreference) {
		if (!this.typePreferences.contains(typePreference)) {
			this.typePreferences.add(typePreference);
		}
		if (typePreference.getPreference() != this) {
			typePreference.updatePreference(this);
		}
	}
	// 연관관계 편의 메서드
	public void addRegionPreference(RegionPreference regionPreference) {
		if (!this.regionPreferences.contains(regionPreference)) {
			this.regionPreferences.add(regionPreference);
		}
		if (regionPreference.getPreference() != this) {
			regionPreference.updatePreference(this);
		}
	}
}
