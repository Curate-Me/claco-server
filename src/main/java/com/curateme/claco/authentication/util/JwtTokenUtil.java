package com.curateme.claco.authentication.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.curateme.claco.member.domain.entity.Member;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @packageName : com.curateme.claco.authentication.util
 * @fileName    : JwtTokenUtil.java
 * @author      : 이 건
 * @date        : 2024.10.17
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.17   	   이 건        최초 생성
 */
public interface JwtTokenUtil {

	// Authentication 으로부터 엑세스 토큰 발급
	String generateAccessToken(Authentication authentication);

	// RefreshToken 생성
	String generateRefreshToken();

	// AccessToken 으로부터 Authentication 발급
	Authentication getAuthentication(String accessToken);

	// Member 엔티티로부터 Authentication 발급
	Authentication createAuthentication(Member member);

	// HttpServletRequest 로부터 엑세스 토큰 추출
	Optional<String> extractAccessToken(HttpServletRequest request);

	// Refresh Token 쿠키에서 추출 메서드
	Optional<String> extractRefreshToken(HttpServletRequest request);

	// 토큰 유효성 검증 메서드
	public boolean validate(String token);

}
