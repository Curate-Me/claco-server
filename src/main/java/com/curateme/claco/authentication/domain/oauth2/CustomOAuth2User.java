package com.curateme.claco.authentication.domain.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import com.curateme.claco.member.domain.entity.Member;

import lombok.Getter;

/**
 * @fileName    : CustomOAuth2User.java
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

	private final Member member;

	public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
		Map<String, Object> attributes, String nameAttributeKey, Member member) {
		super(authorities, attributes, nameAttributeKey);
		this.member = member;
	}

}
