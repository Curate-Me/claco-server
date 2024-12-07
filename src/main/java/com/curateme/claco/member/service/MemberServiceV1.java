package com.curateme.claco.member.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.global.util.S3Util;
import com.curateme.claco.member.domain.dto.request.SignUpRequest;
import com.curateme.claco.member.domain.dto.response.MemberInfoResponse;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.repository.MemberRepository;
import com.curateme.claco.preference.domain.entity.Preference;
import com.curateme.claco.preference.service.PreferenceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author      : 이 건
 * @date        : 2024.10.18
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.18   	   이 건        최초 생성
 * 2024.10.22   	   이 건        예외를 활용한 로직으로 변경, 회원가입 메서드 추가
 * 2024.11.05   	   이 건        회원 정보 조회 / 수정 메서드 추가
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceV1 implements MemberService {

	private final MemberRepository memberRepository;
	private final PreferenceService preferenceService;
	private final SecurityContextUtil securityContextUtil;
	private final S3Util s3Util;

	@Override
	public void checkNicknameValid(String nickname) {
		memberRepository.findMemberByNickname(nickname).stream()
			.findAny()
			.ifPresent(member -> {
				throw new BusinessException(ApiStatus.MEMBER_NICKNAME_DUPLICATE);
			});
	}

	@Override
	public void signUp(SignUpRequest signUpRequest) {

		checkNicknameValid(signUpRequest.getNickname());

		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		member.updateNickname(signUpRequest.getNickname());
		member.updateGender(signUpRequest.getGender());
		member.updateAge(signUpRequest.getAge());
		member.updateRole();

		Preference preference = preferenceService.savePreference(signUpRequest);
		member.updatePreference(preference);

	}

	@Override
	public MemberInfoResponse readMemberInfo() {
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		return MemberInfoResponse.fromEntity(member);
	}

	@Override
	public MemberInfoResponse updateMemberInfo(String updateNickname, MultipartFile updateImage) throws IOException {
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		member.updateNickname(updateNickname);

		String profileImageLocation = "member/profile-image/" + member.getId();

		if (updateImage != null) {
			String url = s3Util.uploadImage(updateImage, profileImageLocation);
			member.updateProfileImage(url);
		}

		return MemberInfoResponse.fromEntity(member);
	}
}
