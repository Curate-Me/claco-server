package com.curateme.claco.preference.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.curateme.claco.authentication.domain.JwtMemberDetail;
import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.member.domain.dto.request.SignUpRequest;
import com.curateme.claco.member.domain.entity.Gender;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;
import com.curateme.claco.preference.domain.dto.request.PreferenceUpdateRequest;
import com.curateme.claco.preference.domain.dto.response.PreferenceInfoResponse;
import com.curateme.claco.preference.domain.entity.Preference;
import com.curateme.claco.preference.domain.entity.RegionPreference;
import com.curateme.claco.preference.domain.entity.TypePreference;
import com.curateme.claco.preference.domain.vo.CategoryPreferenceVO;
import com.curateme.claco.preference.domain.vo.RegionPreferenceVO;
import com.curateme.claco.preference.domain.vo.TypePreferenceVO;
import com.curateme.claco.preference.repository.PreferenceRepository;
import com.curateme.claco.preference.repository.RegionPreferenceRepository;
import com.curateme.claco.preference.repository.TypePreferenceRepository;

/**
 * @author      : 이 건
 * @date        : 2024.10.22
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.22   	   이 건        최초 생성
 */
@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {

	@Mock
	private PreferenceRepository preferenceRepository;
	@Mock
	private SecurityContextUtil securityContextUtil;
	@Mock
	private MemberRepository memberRepository;
	@Mock
	private TypePreferenceRepository typePreferenceRepository;
	@Mock
	private RegionPreferenceRepository regionPreferenceRepository;
	@InjectMocks
	private PreferenceServiceImpl preferenceService;

	@Test
	@DisplayName("선호도 정보 생성 메서드 테스트")
	void savePreferenceTest() {
		// Given
		Long testId = 1L;
		String testString = "test";

		JwtMemberDetail jwtMemberDetail = mock(JwtMemberDetail.class);

		Member testMember = Member.builder()
			.email("test")
			.socialId(testId)
			.role(Role.MEMBER)
			.build();

		List<String> stringList = List.of("test1", "test2", "test3", "test4", "test5");

		SignUpRequest testRequest = SignUpRequest.builder()
			.nickname(testString)
			.age(10)
			.gender(Gender.MALE)
			.minPrice(10)
			.maxPrice(100)
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

		doReturn(jwtMemberDetail).when(securityContextUtil).getContextMemberInfo();
		doReturn(testId).when(jwtMemberDetail).getMemberId();
		doReturn(Optional.of(testMember)).when(memberRepository).findById(testId);
		when(preferenceRepository.save(any(Preference.class))).thenAnswer(invocation -> {
			Preference preference = invocation.getArgument(0);
			return Preference.builder()
				.id(testId)
				.member(preference.getMember())
				.minPrice(preference.getMinPrice())
				.maxPrice(preference.getMaxPrice())
				.preference1(preference.getPreference1())
				.preference2(preference.getPreference2())
				.preference3(preference.getPreference3())
				.preference4(preference.getPreference4())
				.preference5(preference.getPreference5())
				.build();
		});
		when(typePreferenceRepository.save(any(TypePreference.class))).thenReturn(null);
		when(regionPreferenceRepository.save(any(RegionPreference.class))).thenReturn(null);

		// When
		Preference preference = preferenceService.savePreference(testRequest);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(jwtMemberDetail).getMemberId();
		verify(memberRepository).findById(testId);
		verify(preferenceRepository).save(any(Preference.class));

		assertThat(preference.getId()).isEqualTo(testId);
		assertThat(preference.getMember()).isEqualTo(testMember);

	}

	@Test
	@DisplayName("선호도 조회 메서드 테스트")
	void readPreference() {
		// Given
		Long testId = 1L;
		Integer testInt = 0;
		String testString = "test";
		JwtMemberDetail mockMemberDetail = mock(JwtMemberDetail.class);

		Member testMember = Member.builder()
			.socialId(testId)
			.role(Role.MEMBER)
			.email("test@test.com")
			.age(testInt)
			.gender(Gender.MALE)
			.build();

		Preference testPrefer = Preference.builder()
			.preference1(testString)
			.preference2(testString)
			.preference3(testString)
			.preference4(testString)
			.preference5(testString)
			.member(testMember)
			.minPrice(testInt)
			.maxPrice(testInt)
			.build();

		RegionPreference testRegionPrefer = RegionPreference.builder()
			.regionName(testString)
			.preference(testPrefer)
			.build();

		TypePreference testTypePrefer = TypePreference.builder()
			.typeContent(testString)
			.preference(testPrefer)
			.build();
		testPrefer.addRegionPreference(testRegionPrefer);
		testPrefer.addTypeReference(testTypePrefer);
		testMember.updatePreference(testPrefer);

		when(securityContextUtil.getContextMemberInfo()).thenReturn(mockMemberDetail);
		when(mockMemberDetail.getMemberId()).thenReturn(testId);
		when(memberRepository.findMemberByIdIs(testId)).thenReturn(Optional.of(testMember));

		// When
		PreferenceInfoResponse result = preferenceService.readPreference();

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(mockMemberDetail).getMemberId();
		verify(memberRepository).findMemberByIdIs(testId);

		assertThat(result.getAge()).isEqualTo(testInt);
		assertThat(result.getPreferRegions()).hasSize(1);
		assertThat(result.getPreferCategories()).hasSize(5);
		assertThat(result.getPreferTypes()).hasSize(1);

	}

	@Test
	@DisplayName("선호도 수정 메서드 테스트")
	void updatePreference() {
		// Given
		Long testId = 1L;
		Integer testInt = 0;
		String testString = "test";
		Integer resultInt = 1;
		String resultString = "result";
		JwtMemberDetail mockMemberDetail = mock(JwtMemberDetail.class);

		Member testMember = Member.builder()
			.socialId(testId)
			.role(Role.MEMBER)
			.email("test@test.com")
			.age(testInt)
			.gender(Gender.MALE)
			.build();

		Preference testPrefer = Preference.builder()
			.preference1(testString)
			.preference2(testString)
			.preference3(testString)
			.preference4(testString)
			.preference5(testString)
			.member(testMember)
			.minPrice(testInt)
			.maxPrice(testInt)
			.build();

		RegionPreference testRegionPrefer1 = RegionPreference.builder()
			.regionName(testString)
			.preference(testPrefer)
			.build();

		RegionPreference testRegionPrefer2 = RegionPreference.builder()
			.regionName(resultString)
			.preference(testPrefer)
			.build();

		TypePreference testTypePrefer = TypePreference.builder()
			.typeContent(testString)
			.preference(testPrefer)
			.build();
		testPrefer.addRegionPreference(testRegionPrefer1);
		testPrefer.addRegionPreference(testRegionPrefer2);
		testPrefer.addTypeReference(testTypePrefer);
		testMember.updatePreference(testPrefer);

		PreferenceUpdateRequest request = PreferenceUpdateRequest.builder()
			.age(resultInt)
			.gender(Gender.FEMALE)
			.maxPrice(resultInt)
			.minPrice(resultInt)
			.regionPreferences(List.of())
			.typePreferences(List.of(TypePreferenceVO.fromEntity(testTypePrefer), new TypePreferenceVO(resultString)))
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(mockMemberDetail);
		when(mockMemberDetail.getMemberId()).thenReturn(testId);
		when(memberRepository.findMemberByIdIs(testId)).thenReturn(Optional.of(testMember));
		doNothing().when(regionPreferenceRepository).delete(any(RegionPreference.class));
		when(typePreferenceRepository.save(any(TypePreference.class))).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(0));

		// When
		PreferenceInfoResponse result = preferenceService.updatePreference(request);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(mockMemberDetail).getMemberId();
		verify(memberRepository).findMemberByIdIs(testId);
		verify(typePreferenceRepository).save(any(TypePreference.class));

		assertThat(result.getPreferTypes()).hasSize(2);
		assertThat(result.getPreferRegions()).hasSize(0);
		assertThat(result.getAge()).isEqualTo(resultInt);
		assertThat(result.getMinPrice()).isEqualTo(resultInt);
		assertThat(result.getGender()).isEqualTo(Gender.FEMALE);

	}

}