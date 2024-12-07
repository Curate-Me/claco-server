package com.curateme.claco.global.annotation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.curateme.claco.authentication.util.RefreshContextHolder;
import com.curateme.claco.global.response.ApiResponse;

/**
 * 토큰 재생성 체크 AOP
 */
@Aspect
@Component
public class RefreshedAspect {

	@Around("@annotation(com.curateme.claco.global.annotation.TokenRefreshedCheck) || @within(com.curateme.claco.global.annotation.TokenRefreshedCheck)")
	public Object applyTokenRefreshed(ProceedingJoinPoint joinPoint) throws Throwable {
		// 재생성 되었다면 refreshed 필드 변환
		if (RefreshContextHolder.isRefreshed()){
			Object result = joinPoint.proceed();

			if (result instanceof ApiResponse<?> apiResponse) {
				apiResponse.setRefreshed(true);
			}

			return result;
		}
		return joinPoint.proceed();
	}

}
