package com.curateme.claco.global.exception;

import com.curateme.claco.global.response.ApiStatus;

import lombok.Getter;

/**
 * @packageName : com.curateme.claco.global.exception
 * @fileName    : BusinessException.java
 * @author      : 이 건
 * @date        : 2024.10.14
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.14   	   이 건        최초 생성
 */
@Getter
public class BusinessException extends RuntimeException{

	private String code;

	public BusinessException(ApiStatus apiStatus) {
		super(apiStatus.getMessage());
		this.code = apiStatus.getCode();
	}
}
