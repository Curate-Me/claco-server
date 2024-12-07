package com.curateme.claco.global.filter;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiResponse;
import com.curateme.claco.global.response.ApiStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			filterChain.doFilter(request, response);
		} catch (BusinessException exception) {

			log.error("[FilterBusinessException {}] = {}", exception.getCode(), exception.getMessage());

			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");

			response.getWriter()
				.write(objectMapper.writeValueAsString(ApiResponse.fail(exception.getCode(), exception.getMessage())));

		} catch (Exception exception) {

			log.error("[FilterException] = {}", exception.getMessage());

			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setContentType(MediaType.APPLICATION_JSON_VALUE);
			response.setCharacterEncoding("UTF-8");

			response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(ApiStatus.EXCEPTION_OCCUR)));

		}

	}
}
