package com.curateme.claco.review.domain.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.curateme.claco.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@SQLDelete(sql = "UPDATE review_image SET active_status = 'DELETED' WHERE review_image_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
public class ReviewImage extends BaseEntity {

	@Id
	@Column(name = "review_image_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	// 티켓 리뷰 다대일 양방향 매핑
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ticket_review_id")
	private TicketReview ticketReview;
	// 리뷰 이미지 S3 url
	private String imageUrl;


	// TicketReview 연관관계 편의 메서드
	public void updateTicketReview(TicketReview ticketReview) {
		if (this.ticketReview != ticketReview) {
			this.ticketReview = ticketReview;
		}
		if (!ticketReview.getReviewImages().contains(this)) {
			ticketReview.addReviewImage(this);
		}
	}

}
