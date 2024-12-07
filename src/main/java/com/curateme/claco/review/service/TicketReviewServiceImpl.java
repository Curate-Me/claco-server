package com.curateme.claco.review.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.clacobook.repository.ClacoBookRepository;
import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.repository.ConcertRepository;
import com.curateme.claco.global.exception.BusinessException;
import com.curateme.claco.global.response.ApiStatus;
import com.curateme.claco.global.util.S3Util;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.repository.MemberRepository;
import com.curateme.claco.review.domain.dto.request.OrderBy;
import com.curateme.claco.review.domain.dto.request.TicketReviewCreateRequest;
import com.curateme.claco.review.domain.dto.TicketReviewUpdateDto;
import com.curateme.claco.review.domain.dto.response.ReviewInfoResponse;
import com.curateme.claco.review.domain.dto.response.ReviewListResponse;
import com.curateme.claco.review.domain.dto.response.TicketInfoResponse;
import com.curateme.claco.review.domain.dto.response.TicketListResponse;
import com.curateme.claco.review.domain.dto.response.TicketReviewInfoResponse;
import com.curateme.claco.review.domain.entity.PlaceReview;
import com.curateme.claco.review.domain.entity.ReviewImage;
import com.curateme.claco.review.domain.entity.ReviewTag;
import com.curateme.claco.review.domain.entity.TicketReview;
import com.curateme.claco.review.domain.vo.ImageUrlVO;
import com.curateme.claco.review.repository.PlaceCategoryRepository;
import com.curateme.claco.review.repository.PlaceReviewRepository;
import com.curateme.claco.review.repository.ReviewImageRepository;
import com.curateme.claco.review.repository.ReviewTagRepository;
import com.curateme.claco.review.repository.TagCategoryRepository;
import com.curateme.claco.review.repository.TicketReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author      : 이 건
 * @date        : 2024.11.04
 * @author devkeon(devkeon123@gmail.com)
 * ===========================================================
 * DATE               AUTHOR        NOTE
 * -----------------------------------------------------------
 * 2024.11.04		   이 건		   최초 생성
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TicketReviewServiceImpl {

	private final TicketReviewRepository ticketReviewRepository;
	private final SecurityContextUtil securityContextUtil;
	private final MemberRepository memberRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final PlaceCategoryRepository placeCategoryRepository;
	private final PlaceReviewRepository placeReviewRepository;
	private final TagCategoryRepository tagCategoryRepository;
	private final ReviewTagRepository reviewTagRepository;
	private final ConcertRepository concertRepository;
	private final ClacoBookRepository clacoBookRepository;
	private final S3Util s3Util;

	public TicketReviewInfoResponse createTicketReview(TicketReviewCreateRequest request,
		MultipartFile[] multipartFile) throws IOException {
		// 이미지 개수 검사
		if (multipartFile.length > 3) {
			throw new BusinessException(ApiStatus.IMAGE_TOO_MANY);
		}

		// 현재 접근 사용자 조회
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		// claco book 정보 찾기
		ClacoBook clacoBook;
		if (request.getClacoBookId() != null) {
			clacoBook = clacoBookRepository.findByIdIs(request.getClacoBookId()).stream()
				.findAny()
				.orElseThrow(() -> new BusinessException(ApiStatus.CLACO_BOOK_NOT_FOUND));
		} else {
			clacoBook = clacoBookRepository.save(ClacoBook.builder()
				.member(member)
				.title(member.getNickname() + "님의 첫번째 이야기")
				.color("#88888")
				.build());
		}

		if (clacoBook.getTicketReviews().size() >= 20) {
			throw new BusinessException(ApiStatus.EXCEED_SIZE_LIMIT);
		}

		// TicketReview 조립
		TicketReview ticketReview = TicketReview.builder()
			.concert(concertRepository.findById(request.getConcertId())
				.stream()
				.findAny()
				.orElseThrow(() -> new BusinessException(ApiStatus.CONCERT_NOT_FOUND)))
			.member(member)
			.clacoBook(clacoBook)
			.starRate(request.getStarRate())
			.content(request.getContent())
			.watchDate(request.getWatchDate())
			.watchRound(request.getWatchRound())
			.watchSit(request.getWatchSit())
			.casting(request.getCasting())
			.build();

		TicketReview savedTicketReview = ticketReviewRepository.save(ticketReview);

		// PlaceCategory 매핑
		request.getPlaceReviewIds().stream()
			.map(
				placeCategoryVO -> placeCategoryRepository.findById(placeCategoryVO.getPlaceCategoryId())
					.stream()
					.findAny()
					.orElseThrow(() -> new BusinessException(ApiStatus.PLACE_CATEGORY_NOT_FOUND))
			)
			.toList()
			.forEach(
				placeCategory -> {
					PlaceReview placeReview = PlaceReview.builder()
						.ticketReview(savedTicketReview)
						.placeCategory(placeCategory)
						.build();
					PlaceReview savedPlaceReview = placeReviewRepository.save(placeReview);
					savedTicketReview.addPlaceReview(savedPlaceReview);
				}
			);

		// TagCategory 매핑
		request.getTagCategoryIds().stream()
			.map(tagCategoryVO -> tagCategoryRepository.findById(tagCategoryVO.getTagCategoryId())
				.stream()
				.findAny()
				.orElseThrow(() -> new BusinessException(ApiStatus.CONCERT_TAG_NOT_FOUND)))
			.toList()
			.forEach(
				tagCategory -> {
					ReviewTag reviewTag = ReviewTag.builder()
						.ticketReview(savedTicketReview)
						.tagCategory(tagCategory)
						.build();
					ReviewTag savedReviewTage = reviewTagRepository.save(reviewTag);
					savedTicketReview.addReviewTag(savedReviewTage);
				}
			);

		// 리뷰 이미지 생성
		String baseUrl = "review-image/" + savedTicketReview.getId() + "/";
		IntStream.range(0, multipartFile.length).forEach(idx -> {
			MultipartFile file = multipartFile[idx];
			String s3Url;
			try {
				s3Url = s3Util.uploadImage(file, baseUrl + (idx + 1));
			} catch (IOException e) {
				throw new BusinessException(ApiStatus.S3_UPLOAD_ERROR);
			}
			ReviewImage reviewImage = ReviewImage.builder()
				.ticketReview(savedTicketReview)
				.imageUrl(s3Url)
				.build();
			ReviewImage savedReviewImage = reviewImageRepository.save(reviewImage);
			savedTicketReview.addReviewImage(savedReviewImage);
		});

		TicketReviewInfoResponse response = TicketReviewInfoResponse.fromTicketReview(savedTicketReview);
		response.updateClacoBookId(clacoBook.getId());
		return response;
	}

	public ImageUrlVO addNewTicket(Long ticketReviewId, MultipartFile multipartFile) throws IOException {
		// 현재 접근 멤버 조회
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		TicketReview ticketReview = ticketReviewRepository.findById(ticketReviewId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.TICKET_REVIEW_NOT_FOUND));

		// 소유주 체크
		if (ticketReview.getMember() != member) {
			throw new BusinessException(ApiStatus.MEMBER_NOT_OWNER);
		}

		// S3 업로드
		String s3Url = s3Util.uploadImage(multipartFile, "ticket-image/" + ticketReview.getId());
		ticketReview.updateTicketImage(s3Url);
		return ImageUrlVO.fromTicketImage(ticketReview);
	}

	public ReviewInfoResponse readReview(Long reviewId) {
		TicketReview ticketReview = ticketReviewRepository.findTicketReviewById(reviewId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.TICKET_REVIEW_NOT_FOUND));

		return ReviewInfoResponse.fromEntityToDetailReview(ticketReview);
	}

	public TicketReviewInfoResponse readTicketReview(Long ticketReviewId) {
		// 현재 접근 멤버 조회
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		TicketReview ticketReview = ticketReviewRepository.findTicketReviewByIdIs(ticketReviewId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.TICKET_REVIEW_NOT_FOUND));

		TicketReviewInfoResponse response = TicketReviewInfoResponse.fromTicketReview(ticketReview);

		// 소유주 여부 체크
		if (member != ticketReview.getMember()) {
			response.setEditor(false);
		}

		return response;
	}

	public ReviewListResponse readReviewOfConcert(Long concertId, Integer page, Integer size, OrderBy orderBy) {
		// 공연 정보 조회
		Concert concert = concertRepository.findById(concertId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.CONCERT_NOT_FOUND));

		Page<TicketReview> result;

		Pageable pageable = PageRequest.of(page, size);

		// TODO: 동적 쿼리 고려
		if (orderBy.equals(OrderBy.HIGH_RATE)) {
			result = ticketReviewRepository.findAllByConcertOrderByStarRateDescIdDesc(concert, pageable);
		} else if (orderBy.equals(OrderBy.LOW_RATE)) {
			result = ticketReviewRepository.findAllByConcertOrderByStarRateAscIdDesc(concert, pageable);
		} else {
			result = ticketReviewRepository.findAllByConcertOrderByIdDesc(concert, pageable);
		}

		List<ReviewInfoResponse> data = result.get().map(ReviewInfoResponse::fromEntityToSimpleReview).toList();

		return ReviewListResponse.builder()
			.reviewList(data)
			.totalPage(result.getTotalPages())
			.currentPage(result.getNumber() + 1)
			.size(result.getSize())
			.build();
	}

	public TicketListResponse readTicketOfClacoBook(Long clacoBookId) {
		ClacoBook clacoBook = clacoBookRepository.findById(clacoBookId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.CLACO_BOOK_NOT_FOUND));

		List<TicketInfoResponse> infoResponseList = ticketReviewRepository.findByClacoBook(clacoBook).stream()
			.map(TicketInfoResponse::fromEntity)
			.toList();

		return new TicketListResponse(infoResponseList);
	}

	public Integer countReview(Long concertId) {
		// 공연 조회
		Concert concert = concertRepository.findById(concertId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.CONCERT_NOT_FOUND));

		return ticketReviewRepository.countTicketReviewByConcert(concert);
	}

	public TicketReviewUpdateDto editTicketReview(TicketReviewUpdateDto request) {
		// 접근 사용자 조회
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		TicketReview ticketReview = ticketReviewRepository.findById(request.getTicketReviewId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.TICKET_REVIEW_NOT_FOUND));

		// 소유주 조회
		if (ticketReview.getMember() != member) {
			throw new BusinessException(ApiStatus.MEMBER_NOT_OWNER);
		}

		ticketReview.updateWatchSit(request.getWatchSit());
		ticketReview.updateStarRate(request.getStarRate());
		ticketReview.updateContent(request.getContent());

		return TicketReviewUpdateDto.fromEntity(ticketReview);
	}

	public void deleteTicket(Long ticketReviewId) {
		// 접근 사용자 조회
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		TicketReview ticketReview = ticketReviewRepository.findById(ticketReviewId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.TICKET_REVIEW_NOT_FOUND));

		// 소유주 조회
		if (ticketReview.getMember() != member) {
			throw new BusinessException(ApiStatus.MEMBER_NOT_OWNER);
		}

		ticketReviewRepository.delete(ticketReview);

	}

	public void moveTicketReview(Long ticketReviewId, Long clacoBookId) {
		Member member = memberRepository.findById(securityContextUtil.getContextMemberInfo().getMemberId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.MEMBER_NOT_FOUND));

		TicketReview ticketReview = ticketReviewRepository.findById(ticketReviewId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.TICKET_REVIEW_NOT_FOUND));

		// 소유주 조회
		if (ticketReview.getMember() != member) {
			throw new BusinessException(ApiStatus.MEMBER_NOT_OWNER);
		}

		ClacoBook clacoBook = clacoBookRepository.findById(clacoBookId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(ApiStatus.CLACO_BOOK_NOT_FOUND));

		ticketReview.updateClacoBook(clacoBook);
	}
}
