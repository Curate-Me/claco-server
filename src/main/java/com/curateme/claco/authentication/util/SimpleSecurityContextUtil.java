package com.curateme.claco.authentication.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.curateme.claco.authentication.domain.JwtMemberDetail;

import lombok.Getter;

/**
 * @packageName : com.curateme.claco.authentication.util
 * @fileName    : SimpleSecurityContextUtil.java
 * @author      : 이 건
 * @date        : 2024.10.17
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.17   	   이 건        최초 생성
 */
@Getter
@Component
public class SimpleSecurityContextUtil implements SecurityContextUtil {

	/**
	 * 현재 컨텍스트에 있는 유저 정보 반환
	 * @return User 객체에 해당하는 JwtMemberDetail
	 */
	@Override
	public JwtMemberDetail getContextMemberInfo() {
		return (JwtMemberDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

}
