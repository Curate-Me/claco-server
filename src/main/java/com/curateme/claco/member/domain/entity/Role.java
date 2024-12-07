package com.curateme.claco.member.domain.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @packageName : com.curateme.claco.member.entity
 * @fileName    : MemberType.java
 * @author      : 이 건
 * @date        : 2024.10.15
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.15   	   이 건        최초 생성
 * 	2024.10.16		   이 건		   명칭 변경 (MemberType -> MemberRole)
 * 	2024.10.17		   이 건		   명칭 변경 (MemberRoke -> Role)
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Role {

	// 소셜 (회원 가입 중), 회원 (회원 가입 완료), 관리자
	SOCIAL("SOCIAL"), MEMBER("MEMBER"), ADMIN("ADMIN");

	private final String role;

}
