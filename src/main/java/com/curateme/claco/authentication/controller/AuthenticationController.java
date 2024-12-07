package com.curateme.claco.authentication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.curateme.claco.authentication.service.AuthenticationService;
import com.curateme.claco.global.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	/**
	 * 리프레시 쿠키 발급
	 */
	@GetMapping("/refresh")
	public ApiResponse<Void> getRefreshCookie(HttpServletRequest request, HttpServletResponse response) {

		authenticationService.getRefreshToken(request, response);
		return ApiResponse.ok();
	}


}
