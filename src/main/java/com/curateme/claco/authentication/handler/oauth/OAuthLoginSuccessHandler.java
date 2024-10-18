package com.curateme.claco.authentication.handler.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.curateme.claco.authentication.domain.oauth2.CustomOAuth2User;
import com.curateme.claco.authentication.util.JwtTokenUtil;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.member.entity.Member;
import com.curateme.claco.member.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @fileName    : OAuthLoginSuccessHandler.java
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 */
@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtTokenUtil jwtTokenUtil;
	private final MemberRepository memberRepository;

	@Value("${jwt.cookie.expire}")
	private Integer COOKIE_EXPIRATION;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {

		CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();

		Member member = memberRepository.findById(oAuthUser.getMember().getId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		Authentication authentication1 = jwtTokenUtil.createAuthentication(member);

		String accessToken = jwtTokenUtil.generateAccessToken(authentication1);
		String refreshToken = jwtTokenUtil.generateRefreshToken();

		member.updateRefreshToken(refreshToken);

		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
			.path("/")
			.httpOnly(true)
			.sameSite("None")
			.maxAge(COOKIE_EXPIRATION)
			.secure(true)
			.build();

		response.setHeader("Set-Cookie", cookie.toString());

		// TODO: 임시 지정
		String redirectUrl = "http://localhost:5173/oauth/callback/main?token=" +
			URLEncoder.encode(accessToken, StandardCharsets.UTF_8);

		if (member.getRole() == Role.SOCIAL) {
			redirectUrl = "http://localhost:5173/oauth/callback/sign-up?token=" +
				URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
		}

		response.sendRedirect(redirectUrl);
	}
}
