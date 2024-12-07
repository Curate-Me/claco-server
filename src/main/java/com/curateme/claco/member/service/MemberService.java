package com.curateme.claco.member.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.curateme.claco.member.domain.dto.request.SignUpRequest;
import com.curateme.claco.member.domain.dto.response.MemberInfoResponse;

/**
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 * 2024.10.22   	   이 건        메서드 반환 타입 void로 변경(예외 활용에 따라)
 * 2024.11.05   	   이 건        회원 정보 조회 및 수정 메서드 추가
 */
public interface MemberService {

	/**
	 * 닉네임 유효성 체크 (중복 검사)
	 * @param nickname : 검사하고자 하는 닉네임
	 */
	void checkNicknameValid(String nickname);

	/**
	 * 취향 정보, 닉네임 정보를 받아와 회원가입을 완료
	 * @param signUpRequest: nickname, gender, price, preference 정보
	 */
	void signUp(SignUpRequest signUpRequest);

	/**
	 * 회원 정보 불러오기 (닉네임, 이미지 url)
	 * @return 닉네임, 이미지 url
	 */
	MemberInfoResponse readMemberInfo();

	/**
	 * 회원 정보 수정 (닉네임, 이미지)
	 * @param updateNickname : 수정하고자 하는 닉네임
	 * @param updateImage : 이미지 파일
	 * @return : 수정 후 닉네임, 이미지 url
	 */
	MemberInfoResponse updateMemberInfo(String updateNickname, MultipartFile updateImage) throws IOException;

}
