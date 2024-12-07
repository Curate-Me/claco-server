package com.curateme.claco.authentication.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Builder;
import lombok.Getter;

/**
 * @packageName : com.curateme.claco.authentication.domain
 * @fileName    : JwtMemberDetail.java
 * @author      : 이 건
 * @date        : 2024.10.17
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.17   	   이 건        최초 생성
 */
@Getter
public class JwtMemberDetail extends User {

	// member 의 PK 값
	private Long memberId;

	@Builder(builderMethodName = "JwtMemberDetailBuilder")
	public JwtMemberDetail(String username, Collection<? extends GrantedAuthority> authorities, Long memberId) {
		super(username, "", authorities);
		this.memberId = memberId;
	}
}
