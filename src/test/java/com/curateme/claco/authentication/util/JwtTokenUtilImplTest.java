package com.curateme.claco.authentication.util;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.curateme.claco.authentication.domain.JwtMemberDetail;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @packageName : com.curateme.claco.authentication.util
 * @fileName    : JwtTokenUtilImplTest.java
 * @author      : 이 건
 * @date        : 2024.10.17
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.17   	   이 건        최초 생성
 */
@Slf4j
class JwtTokenUtilImplTest {

	private JwtTokenUtil jwtTokenUtil;
	private final String secretKey = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttest";
	private final MemberRepository memberRepository = mock(MemberRepository.class);
	private final Long accessExpiration = 1800000L;
	private final Long refreshExpiration = 604800000L;
	private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

	@BeforeEach
	public void beforeEach() {
		jwtTokenUtil = new JwtTokenUtilImpl(secretKey, accessExpiration, refreshExpiration, memberRepository);
	}

	@Test
	@DisplayName("엑세스 토큰 생성")
	void generateAccessToken() {

		// Given
		Authentication authentication = mock(Authentication.class);
		JwtMemberDetail jwtMemberDetail = mock(JwtMemberDetail.class);
		Long memberId = 1L;

		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_MEMBER");

		doReturn(Collections.singleton(authority)).when(authentication).getAuthorities();
		doReturn(jwtMemberDetail).when(authentication).getPrincipal();
		doReturn(memberId).when(jwtMemberDetail).getMemberId();

		// When
		String accessToken = jwtTokenUtil.generateAccessToken(authentication);

		//Then
		log.info("accessToken={}", accessToken);

		verify(authentication, times(1)).getAuthorities();
		verify(authentication, times(1)).getPrincipal();
		verify(jwtMemberDetail, times(1)).getMemberId();

		// accessToken 형식 검증
		assertThat(accessToken).isNotNull();
		assertThat(accessToken).isNotEmpty();
		assertThat(accessToken.split("\\.")).hasSize(3);

		// accessToken 내용 검증
		Claims payload = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(accessToken.replace("Bearer ", ""))
			.getPayload();

		assertThat(payload.get("auth")).isEqualTo(authority.getAuthority());
		assertThat(Long.valueOf(payload.get("id").toString())).isEqualTo(memberId);

	}

	@Test
	@DisplayName("리프레시 토큰 생성")
	void generateRefreshToken() {

		// When
		String refreshToken = jwtTokenUtil.generateRefreshToken();

		// Then
		log.info("refreshToken={}", refreshToken);

		// refreshToken 형식 검증
		assertThat(refreshToken).isNotNull();
		assertThat(refreshToken).isNotEmpty();
		assertThat(refreshToken.split("\\.")).hasSize(3);

		// refreshToken 내용 검증
		Claims payload = Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(refreshToken)
			.getPayload();

		assertThat(payload.get("sub").toString()).isEqualTo("refreshToken");

	}

	@Test
	@DisplayName("엑세스 토큰으로부터 인증(Authentication) 정보 얻기")
	void getAuthentication() {

		// Given
		Authentication authentication = mock(Authentication.class);
		Long memberId = 1L;
		Member member = Member.builder()
			.id(memberId)
			.email("test")
			.nickname("test")
			.socialId(memberId)
			.role(Role.MEMBER)
			.build();

		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_MEMBER");

		String testAccessToken = Jwts.builder()
			.subject(authentication.getName())
			.claim("auth", authority)
			.claim("id", memberId)
			.signWith(key)
			.expiration(Date.from(Instant.now().plusMillis(accessExpiration)))
			.compact();

		doReturn(Optional.of(member)).when(memberRepository).findById(memberId);

		// When
		Authentication checkAuthentication = jwtTokenUtil.getAuthentication(testAccessToken);

		// Then
		log.info("authority={}", checkAuthentication.getAuthorities());

		verify(memberRepository, times(1)).findById(memberId);
		JwtMemberDetail checkJwtMemberDetail = (JwtMemberDetail)checkAuthentication.getPrincipal();

		assertThat(checkJwtMemberDetail.getMemberId()).isEqualTo(memberId);
		assertThat(checkJwtMemberDetail.getUsername()).isEqualTo("test");

	}

	@Test
	@DisplayName("인증(Authentication) 정보 생성")
	void createAuthentication() {

		// Given
		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_MEMBER");
		Long memberId = 1L;
		Member member = Member.builder()
			.id(memberId)
			.email("test")
			.nickname("test")
			.socialId(memberId)
			.role(Role.MEMBER)
			.build();

		// When
		Authentication authentication = jwtTokenUtil.createAuthentication(member);

		// Then
		JwtMemberDetail jwtMemberDetail = (JwtMemberDetail)authentication.getPrincipal();

		authentication.getAuthorities().forEach((grantedAuthority -> {
			assertThat(grantedAuthority.getAuthority()).isEqualTo(authority.getAuthority());
		}));

		assertThat(jwtMemberDetail.getUsername()).isEqualTo(member.getEmail());
		assertThat(jwtMemberDetail.getMemberId()).isEqualTo(member.getId());

	}

	@Test
	@DisplayName("Request Header 로부터 엑세스 토큰 추출")
	void extractAccessToken() {

		// Given
		HttpServletRequest request = mock(HttpServletRequest.class);

		String testAccessToken = "Bearer " + Jwts.builder()
			.signWith(key)
			.expiration(Date.from(Instant.now().plusMillis(accessExpiration)))
			.compact();

		doReturn(testAccessToken).when(request).getHeader("Authorization");

		// When
		Optional<String> assertToken = jwtTokenUtil.extractAccessToken(request);

		// Then
		verify(request, times(1)).getHeader("Authorization");

		assertThat(assertToken.isEmpty()).isFalse();
		assertThat(assertToken.get()).isEqualTo(testAccessToken.replace("Bearer ", ""));

	}

	@Test
	@DisplayName("쿠키로부터 리프레시 토큰 추출")
	void extractRefreshToken() {

		// Given
		HttpServletRequest request = mock(HttpServletRequest.class);

		String refreshToken = Jwts.builder()
			.subject("refreshToken")
			.expiration(Date.from(Instant.now().plusMillis(refreshExpiration)))
			.signWith(key)
			.compact();
		Cookie[] cookie = {new Cookie("refreshToken", refreshToken)};

		doReturn(cookie).when(request).getCookies();

		// When
		Optional<String> assertToken = jwtTokenUtil.extractRefreshToken(request);

		// Then
		verify(request, times(1)).getCookies();

		assertThat(assertToken.isPresent()).isTrue();
		assertThat(assertToken.get()).isEqualTo(refreshToken);

	}

	// 이전 시간 토큰에 대한 검증
	@Test
	@DisplayName("토큰에 대한 검증(시간, 유효성)")
	void validateExpire() {

		// Given
		String testToken = Jwts.builder()
			.signWith(key)
			.expiration(Date.from(Instant.now().minusMillis(10000L)))
			.compact();

		// When
		boolean validate = jwtTokenUtil.validate(testToken);

		// Then
		assertThat(validate).isFalse();

	}
}