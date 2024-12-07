package com.curateme.claco.global.response;

import org.springframework.http.HttpStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @packageName : com.curateme.claco.global.response
 * @fileName    : ApiStatus.java
 * @author      : 이 건
 * @date        : 2024.10.14
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 	2024.10.14   	   이 건        최초 생성
 * 	2024.10.15		   이 건		   로그인 및 멤버 관련 에러 추가
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiStatus {

	// 성공 응답
	OK(HttpStatus.OK, "COM-000", "Ok"),

	// 서버 에러
	EXCEPTION_OCCUR(HttpStatus.INTERNAL_SERVER_ERROR, "DBG-500", "Something went wrong."),
	RUNTIME_EXCEPTION_OCCUR(HttpStatus.INTERNAL_SERVER_ERROR, "DBG-501", "Something went wrong."),
	EXCEED_SIZE_LIMIT(HttpStatus.BAD_REQUEST, "SZE-001", "Request Size is too big."),

	// 멤버 에러
	MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEM-001", "Member not found."),
	MEMBER_NICKNAME_DUPLICATE(HttpStatus.CONFLICT, "MEM-009", "Nickname is duplicated. Try again."),
	MEMBER_NOT_OWNER(HttpStatus.UNAUTHORIZED, "MEM-999", "Member is not an owner of resource."),

	// 로그인 에러
	ACCESS_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "ACT-001", "AccessToken not found."),
	REFRESH_TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "RFT-001", "RefreshToken not found."),
	MEMBER_LOGIN_SESSION_EXPIRED(HttpStatus.BAD_REQUEST, "MSE-001", "Member login session expired."),
	OAUTH_ATTRIBUTE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ATH-001", "Cannot find OAuth attribute."),

	// 클라코 북 에러
	CLACO_BOOK_NOT_FOUND(HttpStatus.BAD_REQUEST, "CLB-001", "Claco book not found."),
	CLACO_BOOK_CREATION_LIMIT(HttpStatus.BAD_REQUEST, "CLB-010", "Claco Book can create maximum 5."),

	// 티켓 리뷰 에러
	IMAGE_TOO_MANY(HttpStatus.BAD_REQUEST, "TCK-010", "Review image is too many.(limit 3)"),
	TICKET_REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "TCK-001", "Ticket Review not found."),

	// 이미지 에러
	IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "IMG-001", "Image not found."),
	S3_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IMG-010", "Image upload fail."),

	// 공연 에러
	CONCERT_NOT_FOUND(HttpStatus.BAD_REQUEST, "CON-001", "Concert not found."),

	// 장소평 에러
	PLACE_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "PLC-001", "PlaceCategory not found."),

	// 공연 감상 에러
	CONCERT_TAG_NOT_FOUND(HttpStatus.BAD_REQUEST, "CTG-001", "ConcertTage not found.")

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
