package com.curateme.claco.review.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.curateme.claco.authentication.domain.JwtMemberDetail;
import com.curateme.claco.authentication.util.SecurityContextUtil;
import com.curateme.claco.clacobook.domain.entity.ClacoBook;
import com.curateme.claco.clacobook.repository.ClacoBookRepository;
import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.repository.ConcertRepository;
import com.curateme.claco.global.entity.ActiveStatus;
import com.curateme.claco.global.util.S3Util;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;
import com.curateme.claco.review.domain.dto.TicketReviewUpdateDto;
import com.curateme.claco.review.domain.dto.request.OrderBy;
import com.curateme.claco.review.domain.dto.request.TicketReviewCreateRequest;
import com.curateme.claco.review.domain.dto.response.ReviewInfoResponse;
import com.curateme.claco.review.domain.dto.response.ReviewListResponse;
import com.curateme.claco.review.domain.dto.response.TicketListResponse;
import com.curateme.claco.review.domain.dto.response.TicketReviewInfoResponse;
import com.curateme.claco.review.domain.entity.PlaceCategory;
import com.curateme.claco.review.domain.entity.PlaceReview;
import com.curateme.claco.review.domain.entity.ReviewImage;
import com.curateme.claco.review.domain.entity.ReviewTag;
import com.curateme.claco.review.domain.entity.TagCategory;
import com.curateme.claco.review.domain.entity.TicketReview;
import com.curateme.claco.review.domain.vo.ImageUrlVO;
import com.curateme.claco.review.domain.vo.PlaceCategoryVO;
import com.curateme.claco.review.domain.vo.TagCategoryVO;
import com.curateme.claco.review.repository.PlaceCategoryRepository;
import com.curateme.claco.review.repository.PlaceReviewRepository;
import com.curateme.claco.review.repository.ReviewImageRepository;
import com.curateme.claco.review.repository.ReviewTagRepository;
import com.curateme.claco.review.repository.TagCategoryRepository;
import com.curateme.claco.review.repository.TicketReviewRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(MockitoExtension.class)
class TicketReviewServiceTest {

	@Mock
	TicketReviewRepository ticketReviewRepository;
	@Mock
	SecurityContextUtil securityContextUtil;
	@Mock
	MemberRepository memberRepository;
	@Mock
	ReviewImageRepository reviewImageRepository;
	@Mock
	PlaceCategoryRepository placeCategoryRepository;
	@Mock
	PlaceReviewRepository placeReviewRepository;
	@Mock
	TagCategoryRepository tagCategoryRepository;
	@Mock
	ReviewTagRepository reviewTagRepository;
	@Mock
	ConcertRepository concertRepository;
	@Mock
	ClacoBookRepository clacoBookRepository;
	@Mock
	S3Util s3Util;
	@InjectMocks
	TicketReviewServiceImpl ticketReviewService;

	@Test
	@DisplayName("티켓&리뷰 생성")
	void createTicketReview() throws IOException {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		JwtMemberDetail mockMemberDetail = mock(JwtMemberDetail.class);
		Member mockMember = mock(Member.class);
		Concert mockConcert = mock(Concert.class);
		PlaceCategory mockPlaceCategory = mock(PlaceCategory.class);
		TagCategory mockTagCategory = mock(TagCategory.class);
		MultipartFile mockMultipartFile1 = mock(MultipartFile.class);
		MultipartFile mockMultipartFile2 = mock(MultipartFile.class);
		MultipartFile[] mockMultipartFiles = new MultipartFile[] {mockMultipartFile1, mockMultipartFile2};

		TicketReviewCreateRequest request = TicketReviewCreateRequest.builder()
			.placeReviewIds(List.of(PlaceCategoryVO.fromEntity(mockPlaceCategory)))
			.tagCategoryIds(List.of(TagCategoryVO.fromEntity(mockTagCategory)))
			.content(testString)
			.concertId(testId1)
			.watchDate(LocalDate.now())
			.watchRound(testString)
			.starRate(BigDecimal.valueOf(3.5))
			.casting(testString)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(mockMemberDetail);
		when(mockMemberDetail.getMemberId()).thenReturn(testId1);
		when(memberRepository.findById(testId1)).thenReturn(Optional.of(mockMember));

		when(clacoBookRepository.save(any(ClacoBook.class))).thenAnswer(invocation -> (ClacoBook) invocation.getArgument(0));
		when(concertRepository.findById(testId1)).thenReturn(Optional.of(mockConcert));

		when(ticketReviewRepository.save(any(TicketReview.class))).thenAnswer(invocation -> {
			TicketReview ticketReview = invocation.getArgument(0);

			Field createdAtField = TicketReview.class.getSuperclass().getDeclaredField("createdAt");
			createdAtField.setAccessible(true);
			createdAtField.set(ticketReview, LocalDateTime.now());  // 원하는 시간으로 설정

			return ticketReview;
		});

		when(placeCategoryRepository.findById(anyLong())).thenReturn(Optional.of(mockPlaceCategory));
		when(placeReviewRepository.save(any(PlaceReview.class))).thenAnswer(invocation -> (PlaceReview) invocation.getArgument(0));

		when(tagCategoryRepository.findById(anyLong())).thenReturn(Optional.of(mockTagCategory));
		when(reviewTagRepository.save(any(ReviewTag.class))).thenAnswer(invocation -> (ReviewTag) invocation.getArgument(0));

		when(s3Util.uploadImage(any(MultipartFile.class), any(String.class))).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1));
		when(reviewImageRepository.save(any(ReviewImage.class))).thenAnswer(invocation -> (ReviewImage) invocation.getArgument(0));

		// When
		TicketReviewInfoResponse result = ticketReviewService.createTicketReview(request, mockMultipartFiles);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(mockMemberDetail).getMemberId();
		verify(memberRepository).findById(testId1);
		verify(clacoBookRepository).save(any(ClacoBook.class));
		verify(concertRepository).findById(testId1);
		verify(ticketReviewRepository).save(any(TicketReview.class));
		verify(placeCategoryRepository).findById(anyLong());
		verify(placeReviewRepository).save(any(PlaceReview.class));
		verify(tagCategoryRepository).findById(anyLong());
		verify(reviewTagRepository).save(any(ReviewTag.class));
		verify(s3Util, times(2)).uploadImage(any(MultipartFile.class), any(String.class));
		verify(reviewImageRepository, times(2)).save(any(ReviewImage.class));

		assertThat(result.getContent()).isEqualTo(testString);
		assertThat(result.getImageUrlS()).hasSize(2);

	}

	@Test
	@DisplayName("티켓 이미지 업데이트(생성)")
	void addNewTicket() throws IOException {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		JwtMemberDetail mockMemberDetail = mock(JwtMemberDetail.class);
		Member mockMember = mock(Member.class);
		MultipartFile mockMultipartFile = mock(MultipartFile.class);

		TicketReview testTicketReview = TicketReview.builder()
			.id(testId1)
			.member(mockMember)
			.watchRound(testString)
			.watchDate(LocalDate.now())
			.starRate(BigDecimal.valueOf(3.5))
			.content(testString)
			.casting(testString)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(mockMemberDetail);
		when(mockMemberDetail.getMemberId()).thenReturn(testId1);
		when(memberRepository.findById(testId1)).thenReturn(Optional.of(mockMember));
		when(ticketReviewRepository.findById(testId1)).thenReturn(Optional.of(testTicketReview));

		when(s3Util.uploadImage(mockMultipartFile, "ticket-image/1")).thenReturn(testString);

		// When
		ImageUrlVO result = ticketReviewService.addNewTicket(testId1, mockMultipartFile);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(mockMemberDetail).getMemberId();
		verify(memberRepository).findById(testId1);
		verify(ticketReviewRepository).findById(testId1);
		verify(s3Util).uploadImage(mockMultipartFile, "ticket-image/1");

		assertThat(result.getImageUrl()).isEqualTo(testString);

	}

	@Test
	@DisplayName("리뷰 상세 조회")
	void readReview() {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		Member mockMember = mock(Member.class);
		TicketReview testTicketReview = TicketReview.builder()
			.id(testId1)
			.member(mockMember)
			.watchRound(testString)
			.watchDate(LocalDate.now())
			.starRate(BigDecimal.valueOf(3.5))
			.content(testString)
			.casting(testString)
			.build();
		when(ticketReviewRepository.findTicketReviewById(testId1)).thenAnswer(invocation -> {
			Field createdAtField = TicketReview.class.getSuperclass().getDeclaredField("createdAt");
			createdAtField.setAccessible(true);
			createdAtField.set(testTicketReview, LocalDateTime.now());  // 원하는 시간으로 설정
			return Optional.of(testTicketReview);
		});

		// When
		ReviewInfoResponse result = ticketReviewService.readReview(testId1);

		// Then
		verify(ticketReviewRepository).findTicketReviewById(testId1);

		assertThat(result.getContent()).isEqualTo(testString);
		assertThat(result.getTicketReviewId()).isEqualTo(testId1);

	}

	@Test
	@DisplayName("티켓&리뷰 정보 상세 조회")
	void readTicketReview() {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		JwtMemberDetail mockMemberDetail = mock(JwtMemberDetail.class);
		Member mockMember = mock(Member.class);
		Concert mockConcert = mock(Concert.class);
		TicketReview testTicketReview = TicketReview.builder()
			.id(testId1)
			.member(mockMember)
			.watchRound(testString)
			.watchDate(LocalDate.now())
			.starRate(BigDecimal.valueOf(3.5))
			.content(testString)
			.casting(testString)
			.concert(mockConcert)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(mockMemberDetail);
		when(mockMemberDetail.getMemberId()).thenReturn(testId1);
		when(memberRepository.findById(testId1)).thenReturn(Optional.of(mockMember));

		when(ticketReviewRepository.findTicketReviewByIdIs(testId1)).thenAnswer(invocation -> {
			Field createdAtField = TicketReview.class.getSuperclass().getDeclaredField("createdAt");
			createdAtField.setAccessible(true);
			createdAtField.set(testTicketReview, LocalDateTime.now());  // 원하는 시간으로 설정
			return Optional.of(testTicketReview);
		});

		// When
		TicketReviewInfoResponse result = ticketReviewService.readTicketReview(testId1);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(mockMemberDetail).getMemberId();
		verify(memberRepository).findById(testId1);

		assertThat(result.getTicketReviewId()).isEqualTo(testId1);
		assertThat(result.getContent()).isEqualTo(testString);
		assertThat(result.getEditor()).isTrue();

	}

	@Test
	@DisplayName("콘서트의 리뷰 리스트 조회(페이징)")
	void readReviewOfConcert() {
		// Given
		Long testId1 = 1L;
		Concert mockConcert = mock(Concert.class);

		Page<TicketReview> mockPage = mock(Page.class);

		when(concertRepository.findById(testId1)).thenReturn(Optional.of(mockConcert));
		when(ticketReviewRepository.findAllByConcertOrderByIdDesc(any(Concert.class), any(Pageable.class)))
			.thenReturn(mockPage);
		when(mockPage.getSize()).thenReturn(5);

		// When
		ReviewListResponse result1 = ticketReviewService.readReviewOfConcert(testId1, 0, 5, OrderBy.RECENT);

		// Then
		verify(concertRepository).findById(testId1);
		verify(ticketReviewRepository).findAllByConcertOrderByIdDesc(any(Concert.class), any(Pageable.class));

		assertThat(result1.getSize()).isEqualTo(5);

	}

	@Test
	@DisplayName("클라코북의 티켓 리스트 조회")
	void readTicketOfClacoBook() {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		TicketReview mockTicketReview = mock(TicketReview.class);
		ClacoBook mockClacoBook = mock(ClacoBook.class);

		when(mockTicketReview.getTicketImage()).thenReturn(testString);
		when(mockTicketReview.getId()).thenReturn(testId1);
		when(clacoBookRepository.findById(testId1)).thenReturn(Optional.of(mockClacoBook));
		when(ticketReviewRepository.findByClacoBook(mockClacoBook)).thenReturn(
			List.of(mockTicketReview, mockTicketReview));
		// When
		TicketListResponse result = ticketReviewService.readTicketOfClacoBook(testId1);

		// Then
		verify(mockTicketReview, times(2)).getTicketImage();
		verify(mockTicketReview, times(2)).getId();
		verify(clacoBookRepository).findById(testId1);
		verify(ticketReviewRepository).findByClacoBook(mockClacoBook);

		assertThat(result.getTicketList()).hasSize(2);

	}

	@Test
	@DisplayName("리뷰 개수 카운팅")
	void countReview() {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		Concert mockConcert = mock(Concert.class);

		when(concertRepository.findById(testId1)).thenReturn(Optional.of(mockConcert));
		when(ticketReviewRepository.countTicketReviewByConcert(mockConcert)).thenReturn(2);
		// When
		Integer result = ticketReviewService.countReview(testId1);

		// Then
		verify(concertRepository).findById(testId1);
		verify(ticketReviewRepository).countTicketReviewByConcert(mockConcert);

		assertThat(result).isEqualTo(2);

	}

	@Test
	@DisplayName("티켓&리뷰 수정(좌석, 별점, 감상평)")
	void editTicketReview() {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		String beforeString = "hi";
		BigDecimal testBigDecimal = BigDecimal.valueOf(0.0);
		JwtMemberDetail mockMemberDetail = mock(JwtMemberDetail.class);
		Member mockMember = mock(Member.class);

		TicketReviewUpdateDto request = TicketReviewUpdateDto.builder()
			.ticketReviewId(testId1)
			.watchSit(testString)
			.starRate(testBigDecimal)
			.content(testString)
			.build();

		TicketReview testTicketReview = TicketReview.builder()
			.id(testId1)
			.member(mockMember)
			.watchRound(beforeString)
			.watchDate(LocalDate.now())
			.starRate(BigDecimal.valueOf(3.5))
			.content(beforeString)
			.casting(beforeString)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(mockMemberDetail);
		when(mockMemberDetail.getMemberId()).thenReturn(testId1);
		when(memberRepository.findById(testId1)).thenReturn(Optional.of(mockMember));
		when(ticketReviewRepository.findById(testId1)).thenReturn(Optional.of(testTicketReview));

		// When
		TicketReviewUpdateDto result = ticketReviewService.editTicketReview(request);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(mockMemberDetail).getMemberId();
		verify(memberRepository).findById(testId1);
		verify(ticketReviewRepository).findById(testId1);

		assertThat(result.getContent()).isEqualTo(testString);
		assertThat(result.getStarRate()).isEqualTo(testBigDecimal);
		assertThat(result.getWatchSit()).isEqualTo(testString);

	}

	@Test
	@DisplayName("티켓 삭제")
	void deleteTicket() {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		JwtMemberDetail mockMemberDetail = mock(JwtMemberDetail.class);
		Member mockMember = mock(Member.class);
		TicketReview testTicketReview = TicketReview.builder()
			.id(testId1)
			.member(mockMember)
			.watchRound(testString)
			.watchDate(LocalDate.now())
			.starRate(BigDecimal.valueOf(3.5))
			.content(testString)
			.casting(testString)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(mockMemberDetail);
		when(mockMemberDetail.getMemberId()).thenReturn(testId1);
		when(memberRepository.findById(testId1)).thenReturn(Optional.of(mockMember));
		when(ticketReviewRepository.findById(testId1)).thenReturn(Optional.of(testTicketReview));
		doAnswer(invocationOnMock -> {
			TicketReview ticketReview = invocationOnMock.getArgument(0);
			ticketReview.updateActiveStatus(ActiveStatus.DELETED);
			return ticketReview;
		}).when(ticketReviewRepository).delete(testTicketReview);

		// When
		ticketReviewService.deleteTicket(testId1);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(mockMemberDetail).getMemberId();
		verify(memberRepository).findById(testId1);
		verify(ticketReviewRepository).findById(testId1);

		assertThat(testTicketReview.getActiveStatus()).isEqualTo(ActiveStatus.DELETED);

	}

	@Test
	@DisplayName("티켓 리뷰 이동")
	public void moveTicketReview() {
		// Given
		Long testId1 = 1L;
		String testString = "test";
		JwtMemberDetail mockMemberDetail = mock(JwtMemberDetail.class);
		Member mockMember = mock(Member.class);
		ClacoBook mockClacoBook = mock(ClacoBook.class);
		ClacoBook resultClacoBook = mock(ClacoBook.class);
		TicketReview testTicketReview = TicketReview.builder()
			.id(testId1)
			.member(mockMember)
			.clacoBook(mockClacoBook)
			.watchRound(testString)
			.watchDate(LocalDate.now())
			.starRate(BigDecimal.valueOf(3.5))
			.content(testString)
			.casting(testString)
			.build();

		when(securityContextUtil.getContextMemberInfo()).thenReturn(mockMemberDetail);
		when(mockMemberDetail.getMemberId()).thenReturn(testId1);
		when(memberRepository.findById(testId1)).thenReturn(Optional.of(mockMember));
		when(ticketReviewRepository.findById(testId1)).thenReturn(Optional.of(testTicketReview));
		when(clacoBookRepository.findById(testId1)).thenReturn(Optional.of(resultClacoBook));

		// When
		ticketReviewService.moveTicketReview(testId1, testId1);

		// Then
		verify(securityContextUtil).getContextMemberInfo();
		verify(mockMemberDetail).getMemberId();
		verify(memberRepository).findById(testId1);
		verify(ticketReviewRepository).findById(testId1);
		verify(clacoBookRepository).findById(testId1);

		assertThat(testTicketReview.getClacoBook()).isEqualTo(resultClacoBook);

	}
}