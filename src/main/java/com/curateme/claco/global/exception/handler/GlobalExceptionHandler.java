package com.curateme.claco.global.exception.handler;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiResponse;
import com.curateme.claco.global.response.ApiStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * @packageName : com.curateme.claco.global.exception.handler
 * @fileName    : GlobalExceptionHandler.java
 * @author      : 이 건
 * @date        : 2024.10.14
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.14   	   이 건        최초 생성
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	// TODO: 예외의 메시지 반환 부분 변경 (Exception)
	@ExceptionHandler(value = Exception.class)
	public ApiResponse<Void> exceptionHandler(Exception exception) {

		log.error("[Exception] -> message: {}", exception.getMessage());
		log.debug("[Exception] ->", exception);

		return ApiResponse.fail(ApiStatus.EXCEPTION_OCCUR);
	}

	// TODO: 예외의 메시지 반환 부분 변경 (RuntimeException)
	@ExceptionHandler(value = RuntimeException.class)
	public ApiResponse<Void> runtimeExceptionHandler(RuntimeException runtimeException) {

		log.error("[RuntimeException] -> message: {}", runtimeException.getMessage());
		log.debug("[RuntimeException] ->", runtimeException);

		return ApiResponse.fail(ApiStatus.RUNTIME_EXCEPTION_OCCUR);
	}

	@ExceptionHandler(value = BusinessException.class)
	public ApiResponse<Void> businessExceptionHandler(BusinessException businessException) {

		log.error("[BusinessException] -> message: {}", businessException.getMessage());
		log.debug("[BusinessException] ->", businessException);

		return ApiResponse.fail(businessException.getCode(), businessException.getMessage());
	}

}
