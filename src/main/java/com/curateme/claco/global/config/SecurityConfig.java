package com.curateme.claco.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.curateme.claco.authentication.filter.JwtAuthenticationFilter;
import com.curateme.claco.authentication.handler.oauth.OAuthLoginFailureHandler;
import com.curateme.claco.authentication.handler.oauth.OAuthLoginSuccessHandler;
import com.curateme.claco.authentication.service.CustomOAuth2UserService;
import com.curateme.claco.authentication.util.JwtTokenUtil;
import com.curateme.claco.member.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * @fileName    : SecurityConfig.java
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenUtil jwtTokenUtil;
	private final MemberRepository memberRepository;
	private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
	private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
			.formLogin(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.cors((cors) ->
				cors.configurationSource(corsConfiguration()))
			.headers((headers) ->
				headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
			)
			.sessionManagement((sessionManagement) ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.authorizeHttpRequests((authorizeHttpRequests) ->
				authorizeHttpRequests
					.requestMatchers("/probe", "/oauth2/authorization/kakao",
						"/login/oauth2/code/kakao", "/favicon.ico")
					.permitAll()
					.requestMatchers("/api/sign-up/**")
					.hasAnyRole(Role.SOCIAL.getRole(), Role.ADMIN.getRole())
					.requestMatchers("/api/**")
					.hasAnyRole(Role.MEMBER.getRole(), Role.ADMIN.getRole())
					.anyRequest()
					.authenticated()
			)
			.oauth2Login((oauth2Login) ->
				oauth2Login.successHandler(oAuthLoginSuccessHandler)
					.failureHandler(oAuthLoginFailureHandler)
					.userInfoEndpoint((userInfoEndPoint) ->
						userInfoEndPoint.userService(customOAuth2UserService)
					)
			);
		httpSecurity.addFilterBefore(jwtAuthenticationFilter(), LogoutFilter.class);

		return httpSecurity.build();

	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenUtil, memberRepository);
	}

	@Bean
	public CorsConfigurationSource corsConfiguration() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addExposedHeader("Authorization");
		corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		corsConfiguration.setAllowedOrigins(List.of(
			"http://localhost:5173",
			"http://localhost:8080"
		));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", corsConfiguration);

		return source;
	}
}
