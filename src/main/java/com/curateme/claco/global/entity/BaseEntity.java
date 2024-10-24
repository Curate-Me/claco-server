package com.curateme.claco.global.entity;

import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

/**
 * @packageName : com.curateme.claco.global.entity
 * @fileName    : BaseEntity.java
 * @author      : 이 건
 * @date        : 2024.10.15
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.15   	   이 건        최초 생성
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity {

	// 활성 여부
	@Enumerated(value = EnumType.STRING)
	private ActiveStatus activeStatus = ActiveStatus.ACTIVE;
	// 생성일
	private LocalDateTime createdAt;
	// 수정일
	private LocalDateTime updatedAt;

	@PrePersist
	public void createdAt() {
		LocalDateTime currentTime = LocalDateTime.now();
		this.createdAt = currentTime;
		this.updatedAt = currentTime;
	}

	@PreUpdate
	public void updatedAt() {
		this.updatedAt = LocalDateTime.now();
	}

	public void deleteEntity() {
		this.activeStatus = ActiveStatus.DELETED;
	}

	public void restoreEntity() {
		this.activeStatus = ActiveStatus.ACTIVE;
	}

	public void updateActiveStatus(ActiveStatus activeStatus) {
		this.activeStatus = activeStatus;
	}

}
