package com.curateme.claco.authentication.util;

/**
 * 리프레시 여부 확인 ThreadLocal
 */
public class RefreshContextHolder {
	private static final ThreadLocal<Boolean> refreshed = ThreadLocal.withInitial(() -> false);

	public static void setRefreshed(Boolean value) {
		refreshed.set(value);
	}

	public static Boolean isRefreshed() {
		return refreshed.get();
	}

	public static void clear() {
		refreshed.remove();
	}
}
