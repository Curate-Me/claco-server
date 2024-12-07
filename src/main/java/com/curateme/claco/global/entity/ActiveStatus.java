package com.curateme.claco.global.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @packageName : com.curateme.claco.global.entity
 * @fileName    : ActiveStatus.java
 * @author      : 이 건
 * @date        : 2024.10.15
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.15   	   이 건        최초 생성
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ActiveStatus {

	// ACTIVE: 활성, DELETED: 비활성
	ACTIVE("ACTIVE"), DELETED("DELETED");

	private final String activeStatus;

}
