package com.curateme.claco.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import com.curateme.claco.global.filter.ExceptionHandlerFilter;
import com.curateme.claco.member.domain.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenUtil jwtTokenUtil;
	private final MemberRepository memberRepository;
	private final OAuthLoginSuccessHandler oAuthLoginSuccessHandler;
	private final OAuthLoginFailureHandler oAuthLoginFailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final ObjectMapper objectMapper;

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
					.requestMatchers("/health-check", "/oauth2/authorization/kakao",
						"/login/oauth2/code/kakao", "/favicon.ico", "/actuator/**")
					.permitAll()
					.requestMatchers("/swagger-ui/**")
					.permitAll()
					.requestMatchers("/v3/api-docs/**")
					.permitAll()
					.requestMatchers("/api/members/check-nickname")
					.permitAll()
					.requestMatchers(HttpMethod.POST, "/api/members")
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
		httpSecurity.addFilterBefore(exceptionHandlerFilter(), JwtAuthenticationFilter.class);

		return httpSecurity.build();

	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenUtil, memberRepository);
	}

	@Bean
	public ExceptionHandlerFilter exceptionHandlerFilter() {
		return new ExceptionHandlerFilter(objectMapper);
	}

	@Bean
	public CorsConfigurationSource corsConfiguration() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();

		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addExposedHeader("Authorization");
		corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		corsConfiguration.setAllowedOrigins(List.of(
			"http://localhost:5173",
			"http://localhost:8080",
			"https://claco-client.vercel.app"
		));
		corsConfiguration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", corsConfiguration);

		return source;
	}
}
