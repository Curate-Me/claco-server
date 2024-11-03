package com.curateme.claco.review.domain.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
 * @date        : 2024.11.03
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.03		   이 건		   최초 생성
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE review_tag SET active_status = 'DELETED' WHERE review_tag_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
public class ReviewTag {

	@Id @Column(name = "review_tag_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "ticket_review_id")
	private TicketReview ticketReview;

	@ManyToOne
	@JoinColumn(name = "tag_category_id")
	private TagCategory tagCategory;

	// 연관관계 편의 메서드
	public void updateTicketReview(TicketReview ticketReview) {
		if (this.ticketReview != ticketReview) {
			this.ticketReview = ticketReview;
		}
		if (!ticketReview.getReviewTags().contains(this)) {
			ticketReview.addReviewTag(this);
		}
	}
}