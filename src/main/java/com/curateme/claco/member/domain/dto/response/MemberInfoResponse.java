package com.curateme.claco.member.domain.dto.response;

import com.curateme.claco.member.domain.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author : 이 건
 * @date : 2024.11.05
 * @author devkeon(devkeon123 @ gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.05   	   이 건        최초 생성
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfoResponse {

	// 닉네임
	@Schema(description = "회원 설정 닉네임", example = "클라코사용자")
	private String nickname;
	// 이미지 Url
	@Schema(description = "회원 프사 url", example = "https://claco-defaul/default.png")
	private String imageUrl;

	public static MemberInfoResponse fromEntity(Member member) {
		return new MemberInfoResponse(member.getNickname(), member.getProfileImage());
	}

}
