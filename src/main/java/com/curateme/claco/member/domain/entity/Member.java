package com.curateme.claco.member.domain.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.global.entity.BaseEntity;
import com.curateme.claco.preference.domain.entity.Preference;
import com.curateme.claco.review.domain.entity.TicketReview;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @packageName : com.curateme.claco.member.entity
 * @fileName    : Member.java
 * @author      : 이 건
 * @date        : 2024.10.15
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.15   	   이 건        최초 생성
 * 2024.10.16    	   이 건		   빌더 추가 및 MemberType 명칭 변경
 * 2024.10.17		   이 건		   엔티티 필드 제약 조건 변경
 * 2024.10.18		   이 건		   성별 필드 추가 (Gender) 및 Preference 관계 매핑
 * 2024.10.22		   이 건		   나이 필드 추가 및 Preference 매핑 condition 수정
 * 2024.10.24		   이 건		   ClacoBook 일대다 엔티티 매핑, soft delete 조건 추가
 * 2024.10.28		   이 건		   TicketReview	일대다 엔티티 매핑 추가
 * 2024.11.05		   이 건		   닉네임, 프로필 이미지 업데이트 메서드 추가
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET active_status = 'DELETED' WHERE member_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
public class Member extends BaseEntity {

	// auto_increment 사용 id
	@Id @Column(name = "member_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Preference 일대일 양방향 매핑 (주 테이블)
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "preference_id")
	private Preference preference;

	// ClacoBook 일대다 양방향 매핑
	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<ClacoBook> clacoBooks = new ArrayList<>();

	// TicketReview 일대다 양방향 매핑
	@Builder.Default
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<TicketReview> ticketReviews = new ArrayList<>();

	// email
	@NotNull
	@Email
	private String email;
	// 닉네임 (15글자 제약)
	@Column(unique = true, length = 15)
	private String nickname;
	// 소셜 id (카카오)
	@NotNull
	@Column(unique = true)
	private Long socialId;
	// 가입 타입
	@NotNull
	@Enumerated(value = EnumType.STRING)
	private Role role;
	// 성별
	@Enumerated(value = EnumType.STRING)
	private Gender gender;
	// 나이대 (10, 20, 30, 40, 50, 60)
	@Column(length = 2)
	private Integer age;
	// 프로필 이미지 url
	private String profileImage;
	// refresh token
	private String refreshToken;

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void updateNickname(String nickname) {
		if (nickname != null) {
			this.nickname = nickname;
		}
	}

	public void updateProfileImage(String profileImage) {
		if (profileImage != null) {
			this.profileImage = profileImage;
		}
	}

	public void updateGender(Gender gender) {
		if (gender != null) {
			this.gender = gender;
		}
	}

	public void updatePreference(Preference preference) {
		this.preference = preference;
	}

	public void updateAge(Integer age) {
		if (age != null) {
			this.age = age;
		}
	}

	public void updateRole() {
		this.role = Role.MEMBER;
	}

	// 연관관계 편의 메서드
	public void addClacoBook(ClacoBook clacoBook) {
		if (!this.clacoBooks.contains(clacoBook)) {
			this.clacoBooks.add(clacoBook);
		}
		if (clacoBook.getMember() != this) {
			clacoBook.updateMember(this);
		}
	}

	public void addTicketReview(TicketReview ticketReview) {
		if (!this.ticketReviews.contains(ticketReview)) {
			this.ticketReviews.add(ticketReview);
		}
		if (ticketReview.getMember() != this) {
			ticketReview.updateMember(this);
		}
	}


}
