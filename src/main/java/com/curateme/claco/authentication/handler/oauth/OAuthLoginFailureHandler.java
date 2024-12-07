package com.curateme.claco.authentication.handler.oauth;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.curateme.claco.global.response.ApiResponse;
import com.curateme.claco.global.response.ApiStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @fileName    : OAuthLoginFailureHandler.java
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
@RequiredArgsConstructor
public class OAuthLoginFailureHandler implements AuthenticationFailureHandler {
	private final ObjectMapper objectMapper;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException exception) throws IOException, ServletException {

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");

		String responseBody = objectMapper.writeValueAsString(ApiResponse.fail(ApiStatus.MEMBER_NOT_FOUND));
		response.getWriter().write(responseBody);

		log.error("[OauthFail] -> message: {}", exception.getMessage());

	}
}
