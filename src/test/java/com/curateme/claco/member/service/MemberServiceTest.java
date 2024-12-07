package com.curateme.claco.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.curateme.claco.authentication.domain.JwtMemberDetail;
import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.global.util.S3Util;
import com.curateme.claco.member.domain.dto.request.SignUpRequest;
import com.curateme.claco.member.domain.dto.response.MemberInfoResponse;
import com.curateme.claco.member.domain.entity.Gender;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;
import com.curateme.claco.preference.domain.entity.Preference;
import com.curateme.claco.preference.domain.vo.CategoryPreferenceVO;
import com.curateme.claco.preference.domain.vo.RegionPreferenceVO;
import com.curateme.claco.preference.domain.vo.TypePreferenceVO;
import com.curateme.claco.preference.service.PreferenceService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author      : 이 건
 * @date        : 2024.10.22
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.22   	   이 건        최초 생성
 * 2024.11.05   	   이 건        추가 메서드에 대한 테스트 코드 생성
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;
	@Mock
	private SecurityContextUtil securityContextUtil;
	@Mock
	private PreferenceService preferenceService;
	@Mock
	private S3Util s3Util;
	@InjectMocks
	private MemberServiceV1 memberService;

	private final String testString = "test";
	private final Long testLong = 1L;

	@Test
	@DisplayName("닉네임 중복 체크")
	void checkNicknameValid() {
		// Given
		Member testMember = Member.builder()
			.email(testString)
			.nickname(testString)
			.socialId(testLong)
			.role(Role.MEMBER)
			.build();

		when(memberRepository.findMemberByNickname(testString)).thenReturn(Optional.of(testMember));

		// When
		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
			memberService.checkNicknameValid(testString);
		});

		// Then
		verify(memberRepository, times(1)).findMemberByNickname(testString);
		assertThat(exception.getCode()).isEqualTo(ApiStatus.MEMBER_NICKNAME_DUPLICATE.getCode());

	}

	@Test
	@DisplayName("회원가입 테스트")
	void signUpTest() {
		// Given
		List<String> stringList = List.of("test1", "test2", "test3", "test4", "test5");

		Integer testInteger = 10;

		Member testMember = Member.builder()
			.id(testLong)
			.email(testString)
			.socialId(testLong)
			.role(Role.SOCIAL)
			.build();

		Preference preferenceMock = mock(Preference.class);

		SignUpRequest testRequest = SignUpRequest.builder()
			.nickname(testString)
			.age(testInteger)
			.gender(Gender.MALE)
			.minPrice(testInteger)
			.maxPrice(testInteger)
			.categoryPreferences(stringList.stream()
				.map(CategoryPreferenceVO::new)
				.toList())
			.regionPreferences(stringList.stream()
				.map(RegionPreferenceVO::new)
				.toList())
			.typePreferences(stringList.stream()
				.map(TypePreferenceVO::new)
				.toList())
			.build();

		JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

		when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
		when(jwtMemberDetailMock.getMemberId()).thenReturn(testLong);
		when(memberRepository.findById(testLong)).thenReturn(Optional.of(testMember));
		when(preferenceService.savePreference(testRequest)).thenReturn(preferenceMock);

		// When
		memberService.signUp(testRequest);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(jwtMemberDetailMock).getMemberId();
		verify(memberRepository).findById(testLong);
		verify(preferenceService).savePreference(testRequest);

		assertThat(testMember.getNickname()).isEqualTo(testString);
		assertThat(testMember.getPreference()).isEqualTo(preferenceMock);
		assertThat(testMember.getAge()).isEqualTo(testInteger);
		assertThat(testMember.getRole()).isEqualTo(Role.MEMBER);

	}

	@Test
	@DisplayName("회원 정보 조회")
	void readMemberInfo() {
		// Given
		JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);

		Member testMember = Member.builder()
			.id(testLong)
			.email(testString)
			.nickname(testString)
			.profileImage(testString)
			.socialId(testLong)
			.role(Role.SOCIAL)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
		when(jwtMemberDetailMock.getMemberId()).thenReturn(testLong);
		when(memberRepository.findById(testLong)).thenReturn(Optional.of(testMember));

		// When
		MemberInfoResponse result = memberService.readMemberInfo();

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(jwtMemberDetailMock).getMemberId();
		verify(memberRepository).findById(testLong);

		assertThat(result.getNickname()).isEqualTo(testString);
		assertThat(result.getImageUrl()).isEqualTo(testString);

	}

	@Test
	@DisplayName("회원 정보 업데이트")
	void updateMemberInfo() throws IOException {
		// Given
		JwtMemberDetail jwtMemberDetailMock = mock(JwtMemberDetail.class);
		String updateString = "update";
		String testUrlString = "test/url";
		MultipartFile mockFile = mock(MultipartFile.class);

		Member testMember = Member.builder()
			.id(testLong)
			.email(testString)
			.nickname(testString)
			.profileImage(testString)
			.socialId(testLong)
			.role(Role.SOCIAL)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(jwtMemberDetailMock);
		when(jwtMemberDetailMock.getMemberId()).thenReturn(testLong);
		when(memberRepository.findById(testLong)).thenReturn(Optional.of(testMember));
		when(s3Util.uploadImage(any(MultipartFile.class), any(String.class))).thenReturn(testUrlString);

		// When
		MemberInfoResponse result = memberService.updateMemberInfo(updateString, mockFile);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(jwtMemberDetailMock).getMemberId();
		verify(memberRepository).findById(testLong);
		verify(s3Util).uploadImage(any(MultipartFile.class), any(String.class));

		assertThat(result.getNickname()).isEqualTo(updateString);
		assertThat(result.getImageUrl()).isEqualTo(testUrlString);
		assertThat(testMember.getNickname()).isEqualTo(updateString);
		assertThat(testMember.getProfileImage()).isEqualTo(testUrlString);

	}

}