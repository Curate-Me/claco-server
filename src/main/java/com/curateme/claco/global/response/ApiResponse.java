package com.curateme.claco.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {

    @Schema(description = "응답 코드", example = "COM-000")
    private final String code;
    @Schema(description = "응답 메시지", example = "OK.")
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;
    @Schema(description = "토큰 재발급 여부 확인, true: 재발급, false: 재발급 안됨")
    @Setter
    private Boolean refreshed;

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(ApiStatus.OK.getCode(), ApiStatus.OK.getMessage(), null, false);
    }

    // 성공한 경우 응답 생성
    public static <T> ApiResponse<T> ok(T result) {
        return new ApiResponse<>(ApiStatus.OK.getCode(), ApiStatus.OK.getMessage(), result, false);
    }

    // 실패한 경우 응답 생성
    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(code, message, null, false);
    }

    public static <T> ApiResponse<T> fail(ApiStatus apiStatus) {
        return new ApiResponse<>(apiStatus.getCode(), apiStatus.getMessage(), null, false);
    }
}