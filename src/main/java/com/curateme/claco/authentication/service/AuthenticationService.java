package com.curateme.claco.authentication.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curateme.claco.authentication.util.JwtTokenUtil;
import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.repository.MemberRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

	private final MemberRepository memberRepository;
	private final SecurityContextUtil securityContextUtil;
	private final JwtTokenUtil jwtTokenUtil;

	@Value("${jwt.cookie.expire}")
	private Integer COOKIE_EXPIRATION;
	@Value("${front.url}")
	private String frontUrl;
	@Value("${backend.domain}")
	private String backUrl;

	/**
	 * 리프레시 쿠키 생성
	 */
	public void getRefreshToken(HttpServletRequest request, HttpServletResponse response) {

		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		// refresh token 생성 및 업데이트
		String refreshToken = jwtTokenUtil.generateRefreshToken();

		member.updateRefreshToken(refreshToken);

		ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
			.path("/")
			.httpOnly(true)
			.sameSite("None")
			.maxAge(COOKIE_EXPIRATION)
			.secure(true)
			.domain(backUrl)
			.build();

		response.setHeader("Set-Cookie", cookie.toString());
		response.setHeader("Access-Control-Allow-Origin", frontUrl);
		response.setHeader("Access-Control-Allow-Credentials", "true");
	}

}
