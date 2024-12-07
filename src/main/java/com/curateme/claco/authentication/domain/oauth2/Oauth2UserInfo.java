package com.curateme.claco.authentication.domain.oauth2;

import java.util.Map;
import java.util.Optional;

/**
 * @fileName    : Oauth2UserInfo.java
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 */
public abstract class Oauth2UserInfo {

	// OAuth attribute 객체
	protected Map<String, Object> attributes;

	public Oauth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	// OAuth 자체 id (socialId)
	public abstract Long getId(); // 카카오 - "id"

	// email 정보
	public abstract Optional<String> getEmail();

	// 프로필 이미지 정보
	public abstract Optional<String> getProfileImage();
}
