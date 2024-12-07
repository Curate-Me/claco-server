package com.curateme.claco.preference.service;

import com.curateme.claco.member.domain.dto.request.SignUpRequest;
import com.curateme.claco.preference.domain.dto.request.PreferenceUpdateRequest;
import com.curateme.claco.preference.domain.dto.response.PreferenceInfoResponse;
import com.curateme.claco.preference.domain.entity.Preference;

/**
 * @author      : 이 건
 * @date        : 2024.10.22
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.22   	   이 건        최초 생성
 * 2024.11.05   	   이 건        취향 정보 조회, 수정 메서드 추가
 */
public interface PreferenceService {

	/**
	 * 관련 Preference 객체를 생성하고 저장하는 메서드
	 * @param signUpRequest : 저장하고자 하는 Preference 정보를 종합적으로 담고 있는 객체
	 * @return : 저장한 Preference 반환 (Type, Region 제외)
	 */
	Preference savePreference(SignUpRequest signUpRequest);

	/**
	 * 접근한 유저의 취향 정보를 종합해서 조회
	 * @return : 유저 취향 정보 데이터
	 */
	PreferenceInfoResponse readPreference();

	/**
	 * 접근한 유저의 취향 정보를 수정
	 * @param request : 수정할 취향 정보들
	 * @return : 유저 취향 정보 데이터 (수정 후)
	 */
	PreferenceInfoResponse updatePreference(PreferenceUpdateRequest request);

}
