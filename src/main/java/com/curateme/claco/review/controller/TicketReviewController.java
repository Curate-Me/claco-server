package com.curateme.claco.review.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.curateme.claco.review.domain.dto.TicketReviewUpdateDto;
import com.curateme.claco.review.domain.dto.request.OrderBy;
import com.curateme.claco.review.domain.dto.request.TicketReviewCreateRequest;
import com.curateme.claco.review.domain.dto.response.CountResponse;
import com.curateme.claco.review.domain.dto.response.ReviewInfoResponse;
import com.curateme.claco.review.domain.dto.response.ReviewListResponse;
import com.curateme.claco.review.domain.dto.response.TicketListResponse;
import com.curateme.claco.review.domain.dto.response.TicketReviewInfoResponse;
import com.curateme.claco.review.domain.vo.ImageUrlVO;
import com.curateme.claco.review.service.TicketReviewServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@TokenRefreshedCheck
@RestController
@RequestMapping("/api/ticket-reviews")
@RequiredArgsConstructor
public class TicketReviewController {

	private final TicketReviewServiceImpl ticketReviewService;

	/**
	 * TicketReview 생성
	 * @param request : 생성 정보, 클라코북의 경우 초기 생성이라면 null
	 * @param files : 리뷰 이미지 배열
	 * @return : 생성한 TicketReview 정보
	 */
	@PostMapping(consumes = {"multipart/form-data"})
	@Operation(summary = "티켓 초기 생성 (리뷰)", description = "리뷰 입력과 함께 티켓 초기 생성")
	@Parameter(name = "request", description = "생성하고자 하는 요청 본문, form-data text로 보내주세요.")
	@Parameter(name = "files", description = "MultipartFile 배열, 여러 이미지 파일 보내기 가능")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TCK-010", description = "이미지 최대 개수 초과(최대 3)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "사용자를 찾을 수 없음(잘못된 토큰 요청)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLB-001", description = "요청한 클라코북을 찾을 수 없음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SZE-001", description = "클라코북에 저장 가능한 티켓 수를 넘음(최대 20)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CON-001", description = "저장하고자 하는 콘서트를 찾을 수 없음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PLC-001", description = "요청한 장소평을 찾을 수 없음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CTG-001", description = "요청한 공연 성격을 찾을 수 없음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "IMG-010", description = "이미지 업로드에 실패함(S3 연결 확인)"),
	})
	public ApiResponse<TicketReviewInfoResponse> createTicketReview(
		@Validated @RequestPart("request") TicketReviewCreateRequest request,
		@RequestPart(value = "files", required = false) MultipartFile[] files) throws IOException {

		return ApiResponse.ok(ticketReviewService.createTicketReview(request, files == null ? new MultipartFile[]{} : files));
	}

	/**
	 * 티켓 이미지 업로드
	 * @param id : 티켓 이미지를 저장할 TicketReview id
	 * @param file : 이미지 파일
	 * @return : 저장한 이미지 url
	 */
	@PutMapping(value = "/ticket-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "티켓 이미지 저장(티켓리뷰 생성 후)", description = "티켓 이미지 저장 후 이미지 Url 추출")
	@Parameter(name = "id", description = "티켓 이미지가 저장될 티켓리뷰 id")
	@Parameter(name = "file", description = "티켓 이미지 MultipartFile")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TCK-001", description = "티켓리뷰를 찾을 수 없음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "사용자를 찾을 수 없음(잘못된 토큰 요청)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-999", description = "해당 리소스의 소유주가 아님")
	})
	public ApiResponse<ImageUrlVO> addNewTicketImage(
		@RequestParam("id") Long id,
		@RequestPart("file") MultipartFile file) throws IOException {

		return ApiResponse.ok(ticketReviewService.addNewTicket(id, file));
	}

	/**
	 * 단일 리뷰 상세 조회
	 * @param id : 조회하고자 하는 리뷰 id
	 * @return : 리뷰의 정보
	 */
	@GetMapping("/reviews/{reviewId}")
	@Operation(summary = "단일 리뷰 상세 조회", description = "단일 리뷰 상세 조회(티켓리뷰 아님)")
	@Parameter(name = "reviewId", description = "상세 조회할 리뷰 id")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TCK-001", description = "티켓리뷰를 찾을 수 없음")
	})
	public ApiResponse<ReviewInfoResponse> readDetailReview(@PathVariable("reviewId") Long id) {
		return ApiResponse.ok(ticketReviewService.readReview(id));
	}

	/**
	 * TicketReview 정보 상세 조회(공연 정보 포함)
	 * @param id : 조회하고자 하는 TicketReview id
	 * @return : TickerReview 전체 정보
	 */
	@GetMapping("/{ticketReviewId}")
	@Operation(summary = "티켓리뷰 상세 조회 (공연 정보도 같이)", description = "티켓 리뷰 상세 조회 (공연 정보도 같이)")
	@Parameter(name = "ticketReviewId", description = "상세 조회할 티켓리뷰 id")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TCK-001", description = "티켓리뷰를 찾을 수 없음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "사용자를 찾을 수 없음(잘못된 토큰 요청)")
	})
	public ApiResponse<TicketReviewInfoResponse> readTicketReview(@PathVariable("ticketReviewId") Long id) {
		return ApiResponse.ok(ticketReviewService.readTicketReview(id));
	}

	/**
	 * 공연의 리뷰 리스트 조회
	 * @param concertId : 조회하고자 하는 공연 id
	 * @param page : 페이지 번호 (1부터 시작)
	 * @param size : 한 페이지의 크기 (10개 초과시 오류)
	 * @param orderBy : 정렬 조건 (별점 높은 순, 낮은 순, 최신순)
	 * @return : 총 페이지 개수, 현재 페이지 번호, 요청 사이즈, 페이징 된 데이터
	 */
	@GetMapping("/concerts/reviews/{concertId}")
	@Operation(summary = "공연의 리뷰 리스트 조회", description = "공연에 작성된 리뷰 리스트 조회")
	@Parameter(name = "concertId", description = "조회할 공연의 id")
	@Parameter(name = "page", description = "조회할 페이지 (1부터 시작)")
	@Parameter(name = "size", description = "한 페이지의 개수(최대 10)")
	@Parameter(name = "orderBy", description = "정렬 조건, HIGH_RATE / LOW_RATE / RECENT", example = "RECENT")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CON-001", description = "콘서트를 찾을 수 없음")
	})
	public ApiResponse<ReviewListResponse> readReviewOfConcert(@PathVariable("concertId") Long concertId,
		@Validated @Min(value = 1) @RequestParam("page") Integer page,
		@Validated @Max(value = 10) @RequestParam("size") Integer size,
		@RequestParam("orderBy")OrderBy orderBy
		) {
		return ApiResponse.ok(ticketReviewService.readReviewOfConcert(concertId, page - 1, size, orderBy));
	}

	/**
	 * 클라코북에 속한 티켓 조회
	 * @param id : 조회하고자 하는 클라코북 id
	 * @return : 티켓 아이디, 티켓 이미지
	 */
	@GetMapping("/claco-books/{clacoBookId}")
	@Operation(summary = "클라코북에 속한 티켓 리스트 조회", description = "클라코북에 속한 티켓 리스트(이미지)조회")
	@Parameter(name = "clacoBookId", description = "조회할 클라코북 id")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLB-001", description = "요청한 클라코북을 찾을 수 없음")
	})
	public ApiResponse<TicketListResponse> readTicketListFromClacoBook(@PathVariable("clacoBookId") Long id) {
		return ApiResponse.ok(ticketReviewService.readTicketOfClacoBook(id));
	}

	/**
	 * 공연의 총 리뷰 개수
	 * @param id : 조회하고자 하는 공연 id
	 * @return : 리뷰 총 개수
	 */
	@GetMapping("/concerts/{concertId}/size")
	@Operation(summary = "공연의 리뷰 총 개수", description = "공연의 리뷰 총 개수")
	@Parameter(name = "concertId", description = "조회할 공연 id")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CON-001", description = "콘서트를 찾을 수 없음")
	})
	public ApiResponse<CountResponse> countReviewOfConcert(@PathVariable("concertId") Long id) {
		return ApiResponse.ok(new CountResponse(ticketReviewService.countReview(id)));
	}

	@PutMapping
	@Operation(summary = "티켓리뷰 수정", description = "티켓리뷰 수정(관람 좌석, 별점, 감상평)")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TCK-001", description = "티켓리뷰를 찾을 수 없음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "사용자를 찾을 수 없음(잘못된 토큰 요청)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-999", description = "해당 리소스의 소유주가 아님")
	})
	public ApiResponse<TicketReviewUpdateDto> updateTicketReview(@RequestBody TicketReviewUpdateDto reviewUpdateDto) {
		return ApiResponse.ok(ticketReviewService.editTicketReview(reviewUpdateDto));
	}

	/**
	 * TickerReview 삭제 (Cascade)
	 * @param id : 삭제하고자 하는 TickerReview id
	 * @return : Void
	 */
	@DeleteMapping("/{ticketReviewId}")
	@Operation(summary = "티켓리뷰 삭제", description = "티켓리뷰 삭제")
	@Parameter(name = "ticketReviewId", description = "삭제할 티켓리뷰")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COM-000", description = "정상 응답"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "TCK-001", description = "티켓리뷰를 찾을 수 없음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-001", description = "사용자를 찾을 수 없음(잘못된 토큰 요청)"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEM-999", description = "해당 리소스의 소유주가 아님")
	})
	public ApiResponse<Void> deleteTicketReview(@PathVariable("ticketReviewId") Long id) {
		ticketReviewService.deleteTicket(id);
		return ApiResponse.ok();
	}

	@PutMapping("/{ticketReviewId}/claco-books/{clacoBookId}")
	@Operation(summary = "티켓리뷰 이동", description = "티켓리뷰 클라코북 이동")
	@Parameter(name = "ticketReviewId", description = "이동 대상 티켓리뷰 id")
	@Parameter(name = "clacoBookId", description = "이동 대상 클라코북 id")
	public ApiResponse<Void> moveTicketReview(@PathVariable("ticketReviewId") Long ticketReviewId,
		@PathVariable("clacoBookId") Long clacoBookId) {
		ticketReviewService.moveTicketReview(ticketReviewId, clacoBookId);
		return ApiResponse.ok();
	}

}
