package com.curateme.claco.clacobook.domain.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.curateme.claco.global.entity.BaseEntity;
import com.curateme.claco.member.domain.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.10.23
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.23   	   이 건        최초 생성
 * 2024.10.24   	   이 건        Member 연관관계 편의 메서드 추가
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE claco_book SET active_status = 'DELETED' WHERE claco_book_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
public class ClacoBook extends BaseEntity {

	@Id @Column(name = "claco_book_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// 제목
	@NotNull
	private String title;
	// 클라코북 색깔
	@NotNull
	private String color;

	// Member 다대일 양방향 매핑 (주 테이블)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	public void updateTitle(String title) {
		this.title = title;
	}

	public void updateColor(String color) {
		this.color = color;
	}

	// 연관관계 편의 메서드
	public void updateMember(Member member) {
		if (this.member != member) {
			this.member = member;
		}
		if (!member.getClacoBooks().contains(this)) {
			member.addClacoBook(this);
		}
	}

}
