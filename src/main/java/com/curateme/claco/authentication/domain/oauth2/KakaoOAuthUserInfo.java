package com.curateme.claco.authentication.domain.oauth2;

import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/**
 * @fileName    : KakaoOAuthUserInfo.java
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 */
@Slf4j
public class KakaoOAuthUserInfo extends Oauth2UserInfo{

	public KakaoOAuthUserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	// OAuth 자체 id (socialId)
	@Override
	public Long getId() {
		return (Long) attributes.get("id");
	}

	// email 정보
	@Override
	public Optional<String> getEmail() {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

		if (kakaoAccount == null) return Optional.empty();

		boolean isEmailVerified = (boolean) kakaoAccount.get("is_email_verified");
		boolean isEmailValid = (boolean) kakaoAccount.get("is_email_valid");

		if (!isEmailValid || !isEmailVerified) return Optional.empty();

		return Optional.of((String) kakaoAccount.get("email"));
	}

	// 프로필 이미지 정보
	@Override
	public Optional<String> getProfileImage() {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

		if (kakaoAccount == null) return Optional.empty();

		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		if (profile == null) return Optional.empty();

		return Optional.of((String) profile.get("thumbnail_image_url"));
	}
}
