package com.curateme.claco.member.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.curateme.claco.global.annotation.TokenRefreshedCheck;
import com.curateme.claco.global.response.ApiResponse;
import com.curateme.claco.member.domain.dto.request.SignUpRequest;
import com.curateme.claco.member.domain.dto.response.MemberInfoResponse;
import com.curateme.claco.member.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

/**
 * @author      : 이 건
 * @date        : 2024.10.22
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.10.22   	   이 건        최초 생성
 * 2024.11.05   	   이 건        회원 정보 조회 및 수정 추가, Swagger 적용
 */
@TokenRefreshedCheck
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	/**
	 * GET /api/members/nickname
	 * 닉네임 유효성 체크
	 * @param nickname : 체크하고자 하는 닉네임
	 * @return : COM-000 정상, MEM-009 닉네임 중복
	 */
	@Operation(summary = "닉네임 중복 조회", description = "닉네임 중복 조회")
	@Parameter(name = "nickname", description = "체크하고자 하는 닉네임(필수)")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-009", description = "중복된 닉네임")
	})
	@GetMapping("/check-nickname")
	public ApiResponse<Void> checkNicknameDuplicate(@RequestParam("nickname") String nickname) {
		memberService.checkNicknameValid(nickname);

		return ApiResponse.ok();
	}

	/**
	 * GET /api/members
	 * 유저 정보 조회
	 * @return : 유저 정보 (닉네임, 프사)
	 */
	@Operation(summary = "유저 정보 조회", description = "유저 정보 조회, 토큰 포함 요청 필요")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "조회하고자 하는 사용자를 찾을 수 없음(잘못된 토큰 정보)")
	})
	@GetMapping
	public ApiResponse<MemberInfoResponse> readMemberInfo() {
		return ApiResponse.ok(memberService.readMemberInfo());
	}

	/**
	 * POST /api/members/sign-up
	 * 회원가입 정보 입력
	 * @param request : 회원가입하고자 하는 정보 (닉네임, 선호 정보)
	 * @return : COM-000 정상, MEM-009 닉네임 중복
	 */
	@Operation(summary = "회원 가입 메서드(카카오 이후 취향 입력)", description = "카카오 로그인 이후 회원 가입 메서드, 닉네임 중복 검사 완료 되어야함")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "가입하고자 하는 사용자를 찾을 수 없음(잘못된 토큰 정보)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-009", description = "중복된 닉네임")
	})
	@PostMapping
	public ApiResponse<Void> signUp(@RequestBody SignUpRequest request) {
		memberService.signUp(request);

		return ApiResponse.ok();
	}

	/**
	 *
	 * @param updateNickname : 업데이트 하고자 하는 닉네임, 그대로면 null
	 * @param updateImage : 업데이트 하고자 하는 프사, 그대로면 null
	 * @return
	 * @throws IOException
	 */
	@Operation(summary = "유저 정보 수정", description = "유저 정보 수정 api(닉네임, 이미지)")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "수정하고자 하는 사용자를 찾을 수 없음(잘못된 토큰 정보)")
	})
	@Parameter(name = "updateNickname", description = "업데이트 하고자 하는 닉네임, 그대로여도 key-value에서 key는 보내고 value는 null로", required = true)
	@Parameter(name = "updateImage", description = "업데이트 하고자 하는 새로운 이미지(멀티 파트 파일), 그대로여도 key-value에서 key는 보내고 value는 null로", required = true)
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<MemberInfoResponse> updateMemberInfo(
		@RequestPart(value = "updateNickname", required = false) String updateNickname,
		@RequestPart(value = "updateImage", required = false)MultipartFile updateImage
	) throws IOException {
		return ApiResponse.ok(memberService.updateMemberInfo(updateNickname, updateImage));
	}

}
