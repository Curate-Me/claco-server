package com.curateme.claco.authentication.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curateme.claco.authentication.domain.oauth2.CustomOAuth2User;
import com.curateme.claco.authentication.domain.oauth2.KakaoOAuthAttribute;
import com.curateme.claco.member.entity.Member;
import com.curateme.claco.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @fileName    : CustomOAuth2UserService.java
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		log.info("[OAuth2.0] -> service start: clientId={}", userRequest.getClientRegistration().getClientId());

		DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();

		Map<String, Object> attributes = oAuth2User.getAttributes();

		KakaoOAuthAttribute extractAttributes = KakaoOAuthAttribute.of(userNameAttributeName, attributes);

		Member createdUser = getMember(extractAttributes);

		log.info("[OAuth2.0] -> service end: clientId={} / socialId={}", userRequest.getClientRegistration().getClientId(), createdUser.getSocialId());

		return new CustomOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority("ROLE_" + createdUser.getRole().getRole())),
			attributes,
			extractAttributes.getNameAttributeKey(),
			createdUser
		);

	}

	private Member getMember(KakaoOAuthAttribute kakaoOAuthAttribute) {

		Optional<Member> findUser = memberRepository.findMemberBySocialId(kakaoOAuthAttribute.getOauth2UserInfo().getId());

		if(findUser.isEmpty()) {
			Member member = saveMember(kakaoOAuthAttribute);

			return member;
		}

		return findUser.get();

	}

	private Member saveMember(KakaoOAuthAttribute attributes) {
		Member createdUser = attributes.toEntity(attributes.getOauth2UserInfo());
		// TODO: Claco book 기본 생성 추가 필요
		return memberRepository.save(createdUser);
	}
}
