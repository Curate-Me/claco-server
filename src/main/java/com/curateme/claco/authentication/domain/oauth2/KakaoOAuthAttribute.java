package com.curateme.claco.authentication.domain.oauth2;

import java.util.Map;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;

import lombok.Builder;
import lombok.Getter;

/**
 * @fileName    : KakaoOAuthAttribute.java
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 * 2024.11.05   	   이 건        기본 이미지 url 추가
 */
@Getter
public class KakaoOAuthAttribute {

	private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
	private Oauth2UserInfo oauth2UserInfo;
	private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
	private String baseProfileImage = "https://claco-image-bucket.s3.ap-northeast-2.amazonaws.com/member/profile-image/basic-profile.png";

	@Builder
	private KakaoOAuthAttribute(String nameAttributeKey, Oauth2UserInfo oauth2UserInfo) {
		this.nameAttributeKey = nameAttributeKey;
		this.oauth2UserInfo = oauth2UserInfo;
	}

	public KakaoOAuthAttribute(String nameAttributeKey) {
		this.nameAttributeKey = nameAttributeKey;
	}

	// 정적 팩토리 메서드
	public static KakaoOAuthAttribute of(String nameAttributeKey, Map<String, Object> attribute) {
		return KakaoOAuthAttribute.builder()
			.nameAttributeKey(nameAttributeKey)
			.oauth2UserInfo(new KakaoOAuthUserInfo(attribute))
			.build();
	}

	// 엔티티 변환 메서드
	public Member toEntity(Oauth2UserInfo kakaoOAuthUserInfo) {
		return Member.builder()
			.email(kakaoOAuthUserInfo.getEmail().orElseThrow(() -> new BusinessException(ApiStatus.OAUTH_ATTRIBUTE_ERROR)))
			.profileImage(kakaoOAuthUserInfo.getProfileImage().orElse(baseProfileImage))
			.socialId(kakaoOAuthUserInfo.getId())
			.role(Role.SOCIAL)
			.build();
	}
}
