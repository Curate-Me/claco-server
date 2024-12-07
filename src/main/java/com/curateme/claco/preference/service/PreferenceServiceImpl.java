package com.curateme.claco.preference.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.member.domain.dto.request.SignUpRequest;
import com.curateme.claco.member.domain.entity.Member;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

/**
 * @author      : 이 건
 * @date        : 2024.10.22
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.22   	   이 건        최초 생성
 * 2024.11.05   	   이 건        조회 및 수정 메서드 추가
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PreferenceServiceImpl implements PreferenceService {

	private final SecurityContextUtil securityContextUtil;
	private final MemberRepository memberRepository;
	private final PreferenceRepository preferenceRepository;
	private final TypePreferenceRepository typePreferenceRepository;
	private final RegionPreferenceRepository regionPreferenceRepository;

	@Value("${cloud.ai.url}")
	private String URL;

	@Override
	public Preference savePreference(SignUpRequest signUpRequest) {

		// 현재 로그인 세션 유저 정보 추출
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		// AI Server 로 취향 전송하기
		List<String> preferences = signUpRequest.getCategoryPreferences().stream()
			.map(pref -> pref.getPreferenceCategory())
			.collect(Collectors.toList());

		sendPreferencesToAI(member.getId(), preferences);

		// TODO: 개선 필요
		// Preference 생성
		Preference preference = Preference.builder()
			.member(member)
			.minPrice(signUpRequest.getMinPrice())
			.maxPrice(signUpRequest.getMaxPrice())
			.preference1(signUpRequest.getCategoryPreferences().get(0).getPreferenceCategory())
			.preference2(signUpRequest.getCategoryPreferences().get(1).getPreferenceCategory())
			.preference3(signUpRequest.getCategoryPreferences().get(2).getPreferenceCategory())
			.preference4(signUpRequest.getCategoryPreferences().get(3).getPreferenceCategory())
			.preference5(signUpRequest.getCategoryPreferences().get(4).getPreferenceCategory())
			.build();

		Preference savePreference = preferenceRepository.save(preference);

		// TypePreference 저장
		signUpRequest.getTypePreferences().stream()
			.map(TypePreferenceVO::getPreferenceType)
			.map(TypePreference::of)
			.forEach(typePreference -> {
				typePreference.updatePreference(savePreference);
				typePreferenceRepository.save(typePreference);
			});

		// RegionPreference 저장
		signUpRequest.getRegionPreferences().stream()
			.map(RegionPreferenceVO::getPreferenceRegion)
			.map(RegionPreference::of)
			.forEach(regionPreference -> {
				regionPreference.updatePreference(savePreference);
				regionPreferenceRepository.save(regionPreference);
			});

		return savePreference;
	}

	@Override
	public PreferenceInfoResponse readPreference() {

		// 현재 로그인 세션 유저 정보 추출(취향 정보 조회)
		Member member = memberRepository.findMemberByIdIs(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		Preference preference = member.getPreference();

		return PreferenceInfoResponse.builder()
			.age(member.getAge())
			.gender(member.getGender())
			.maxPrice(preference.getMaxPrice())
			.minPrice(preference.getMinPrice())
			.preferCategories(
				Stream.of(preference.getPreference1(), preference.getPreference2(), preference.getPreference3(),
					preference.getPreference4(), preference.getPreference5())
					.map(CategoryPreferenceVO::new)
					.toList()
			)
			.preferTypes(preference.getTypePreferences().stream()
				.map(TypePreferenceVO::fromEntity)
				.toList()
			)
			.preferRegions(preference.getRegionPreferences().stream()
				.map(RegionPreferenceVO::fromEntity)
				.toList()
			)
			.build();
	}

	@Override
	public PreferenceInfoResponse updatePreference(PreferenceUpdateRequest request) {
		// 현재 로그인 세션 유저 정보 추출(취향 정보 조회)
		Member member = memberRepository.findMemberByIdIs(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		// 나이 수정
		member.updateAge(request.getAge());
		// 성별 수정
		member.updateGender(request.getGender());

		Preference preference = member.getPreference();

		// 가격 수정
		preference.updateMinPrice(request.getMinPrice());
		preference.updateMaxPrice(request.getMaxPrice());

		//-- 타입 선호도 교체 시작 --//
		List<String> requestTypeList = request.getTypePreferences()
			.stream()
			.map(TypePreferenceVO::getPreferenceType)
			.toList();

		List<String> currentTypeList = preference.getTypePreferences()
			.stream()
			.map(TypePreference::getTypeContent)
			.toList();

		// 새로운 타입 선호도 생성 및 저장
		requestTypeList.forEach(requestType -> {
			if (!currentTypeList.contains(requestType)) {
				TypePreference newType = TypePreference.builder()
					.preference(preference)
					.typeContent(requestType)
					.build();
				TypePreference savedType = typePreferenceRepository.save(newType);
				preference.addTypeReference(savedType);
			}
		});

		// 기존 타입 선호도 삭제
		Iterator<TypePreference> typeIterator = preference.getTypePreferences().iterator();
		while (typeIterator.hasNext()) {
			TypePreference typePreference = typeIterator.next();
			if (!requestTypeList.contains(typePreference.getTypeContent())){
				typePreferenceRepository.delete(typePreference);
				typeIterator.remove();
			}
		}
		//-- 타입 선호도 교체 종료 --//

		//-- 지역 선호도 교체 시작 --//
		List<String> requestRegionList = request.getRegionPreferences()
			.stream()
			.map(RegionPreferenceVO::getPreferenceRegion)
			.toList();

		List<String> currentRegionList = preference.getRegionPreferences()
			.stream()
			.map(RegionPreference::getRegionName)
			.toList();

		// 새로운 지역 선호도 생성 및 저장
		requestRegionList.forEach(requestType -> {
			if (!currentRegionList.contains(requestType)) {
				RegionPreference newType = RegionPreference.builder()
					.preference(preference)
					.regionName(requestType)
					.build();
				RegionPreference savedType = regionPreferenceRepository.save(newType);
				preference.addRegionPreference(savedType);
			}
		});

		// 기존 지역 선호도 삭제
		Iterator<RegionPreference> regionIterator = preference.getRegionPreferences().iterator();
		while (regionIterator.hasNext()) {
			RegionPreference regionPreference = regionIterator.next();
			if (!requestRegionList.contains(regionPreference.getRegionName())){
				regionPreferenceRepository.delete(regionPreference);
				regionIterator.remove();
			}
		}

		//-- 지역 선호도 교체 종료 --//

		return PreferenceInfoResponse.builder()
			.age(member.getAge())
			.gender(member.getGender())
			.maxPrice(preference.getMaxPrice())
			.minPrice(preference.getMinPrice())
			.preferCategories(
				Stream.of(preference.getPreference1(), preference.getPreference2(), preference.getPreference3(),
						preference.getPreference4(), preference.getPreference5())
					.map(CategoryPreferenceVO::new)
					.toList()
			)
			.preferTypes(preference.getTypePreferences().stream()
				.map(TypePreferenceVO::fromEntity)
				.toList()
			)
			.preferRegions(preference.getRegionPreferences().stream()
				.map(RegionPreferenceVO::fromEntity)
				.toList()
			)
			.build();
	}



	public void sendPreferencesToAI(Long userId, List<String> preferences) {
		Logger logger = LoggerFactory.getLogger(this.getClass());

		// Prepare JSON body for Flask API
		String FLASK_API_URL = URL + "/users/preferences";
		Map<String, Object> body = new HashMap<>();
		body.put("userId", userId);
		body.put("preferences", preferences);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		RestTemplate restTemplate = new RestTemplate();
		try {
			ResponseEntity<String> response = restTemplate.exchange(FLASK_API_URL, HttpMethod.POST, requestEntity, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				logger.info("취향 전송이 완료되었습니다: {}", response.getBody());
			} else {
				logger.warn("취향 전송에 실패했습니다. Status code: {}", response.getStatusCode());
			}
		} catch (Exception e) {
			logger.error("취향 전송에 실패했습니다: {}", e.getMessage(), e);
		}
	}
}
