package com.curateme.claco.global.response.code.status;

import com.curateme.claco.global.response.code.BaseErrorCode;
import com.curateme.claco.global.response.code.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증되지 않은 요청입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),

    // 직원 관련
    _EMPLOYEE_NOT_FOUND(HttpStatus.NOT_FOUND, "EMPLOYEE400", "요청한 직원 정보를 찾을 수 없습니다.."),
    _EMPLOYEE_DUPLICATED_ID(HttpStatus.BAD_REQUEST, "EMPLOYEE401", "중복된 아이디입니다."),

    // 관리자 관련
    _ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "ADMIN400", "요청한 관리자 정보를 찾을 수 없습니다.."),

    // 어린이집 관련
    _KDG_NOT_FOUND(HttpStatus.NOT_FOUND, "KDG400", "해당 어린이집을 찾을 수 없습니다."),
    _KDG_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "KDG400", "해당 어린이집의 이미지를 찾을 수 없습니다."),
    _KDG_ClASS_NOT_FOUND(HttpStatus.NOT_FOUND, "KDG400", "해당 어린이집의 반 정보를 찾을 수 없습니다."),

    // 신청서 관련
    _APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION001", "해당 신청서를 찾을 수 없습니다."),
    _APPLICATION_NOT_CREATED(HttpStatus.NOT_FOUND, "APPLICATION002", "작성된 신청서가 없습니다."),
    _DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION003", "해당 문서를 찾을 수 없습니다."),

    // 공사사항 관련
    _NOTICE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "NOTICE400", "공지사항을 찾을 수 없습니다."),

    // JWT 관련
    _JWT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "JWT400", "Header에 AccessToken 이 존재하지 않습니다."),
    _JWT_INVALID(HttpStatus.UNAUTHORIZED, "JWT401", "검증되지 않는 AccessToken 입니다."),
    _JWT_BLACKLIST(HttpStatus.UNAUTHORIZED, "JWT402", "블랙 리스트에 존재하는 토큰입니다. 다시 로그인 해주세요"),
    _JWT_REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "JWT403",
            "Header에 RefreshToken이 존재하지 않습니다."),
    _JWT_DIFF_REFRESH_TOKEN_IN_REDIS(HttpStatus.UNAUTHORIZED, "JWT404",
            "Redis에 존재하는 Refresh Token과 다릅니다."),
    _JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT405", "만료된 AccessToken 입니다."),

    // AUTH 관련
    _AUTH_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AUTH400", "잘못된 비밀번호입니다. 다시 입력해주세요."),

    // Mail 관련
    _MAIL_CREATE_CODE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL500",
            "인증 코드 생성 중 서버 에러가 발생했습니다."),
    _MAIL_WRONG_CODE(HttpStatus.BAD_REQUEST, "MAIL400", "올바른 인증코드가 아닙니다."),
    _MAIL_LOTTERY_RESULT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL501",
            "추첨 결과 메일 전송 중 서버 에러가 발생했습니다."),

    // S3 관련
    _S3_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "S3400", "S3에 존재하지 않는 이미지입니다."),

    // qna 관련
    _QNA_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "QNA400", "qna를 찾을 수 없습니다."),
    _QNA_NO_READ_PERMISSION(HttpStatus.FORBIDDEN, "QNA403", "읽기 권한이 없는 QNA입니다."),

    //추첨 관련
    _LOTTERY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "LOTTERY400", "lottery를 찾을 수 없습니다."),
    _LOTTERY_ALREADY_DONE(HttpStatus.INTERNAL_SERVER_ERROR, "LOTTERY400", "이미 추첨이 진행된 lottery입니다."),

    // 모집 관련
    _RECRUIT_NOT_FOUND(HttpStatus.NOT_FOUND, "RECRUIT400", "recruit를 찾을 수 없습니다."),
    _RECRUIT_EXPORT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "RECRUIT500",
            "모집 결과 다운 중 서버 에러가 발생했습니다."),
    _RECRUIT_CANNOT_CANCEL(HttpStatus.INTERNAL_SERVER_ERROR, "RECRUIT400", "모집을 취소 할 수 없습니다."),


    _EXTERNAL_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "LABMDA400",
            "LAMBDA 확률 에측 서비스에 연결할 수 없습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDto getReason() {
        return ErrorReasonDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return ErrorReasonDto.builder()
                .httpStatus(httpStatus)
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }
}