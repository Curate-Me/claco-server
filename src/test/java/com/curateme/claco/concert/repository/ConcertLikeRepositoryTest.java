package com.curateme.claco.concert.repository;

import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.domain.entity.ConcertLike;
import com.curateme.claco.member.domain.entity.Member;
import com.curateme.claco.member.domain.entity.Role;
import com.curateme.claco.member.repository.MemberRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ConcertLikeRepositoryTest {

    @Autowired
    private ConcertLikeRepository concertLikeRepository;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        // 1. Member 생성 및 저장
        Member testMember = memberRepository.save(
            Member.builder()
                .email("test@test.com")
                .role(Role.MEMBER)
                .socialId(1L)
                .build()
        );

        // 2. Concert 생성 및 저장
        Concert concert1 = concertRepository.save(
            Concert.builder()
                .mt20id("C12345")
                .prfnm("Concert A")
                .prfpdfrom(LocalDate.of(2024, 1, 1))
                .prfpdto(LocalDate.of(2024, 1, 31))
                .fcltynm("Grand Theater")
                .build()
        );


        // 3. ConcertLike 생성 및 저장
        concertLikeRepository.save(
            ConcertLike.builder()
                .member(testMember) // 이미 저장된 testMember를 사용
                .concert(concert1)
                .build()
        );

    }



    @Test
    void testExistsByConcertId() {
        // Given
        Long concertId = concertRepository.findAll().get(0).getId();

        // When
        boolean exists = concertLikeRepository.existsByConcertId(concertId);

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void testFindMostRecentLikedConcert() {
        // Given
        Long userId = memberRepository.findAll().get(0).getId();

        // When
        Pageable pageable = PageRequest.of(0, 1);
        Long concertId = concertLikeRepository.findMostRecentLikedConcert(userId, pageable).getContent().stream().findFirst().orElse(null);

        // Then
        assertThat(concertId).isNotNull();
    }

    @Test
    void testFindConcertIdsByMemberId() {
        // Given
        Long userId = memberRepository.findAll().get(0).getId();

        // When
        List<Long> concertIds = concertLikeRepository.findConcertIdsByMemberId(userId);

        // Then
        assertThat(concertIds).isNotNull();
        assertThat(concertIds).isNotEmpty();
    }

    @Test
    void testFindTopConcertIdsByLikeCount() {
        // Given
        int topCount = 5;

        // When
        List<Long> topConcertIds = concertLikeRepository.findTopConcertIdsByLikeCount(Pageable.ofSize(topCount));

        // Then
        assertThat(topConcertIds).isNotNull();
        assertThat(topConcertIds).hasSizeLessThanOrEqualTo(topCount);
    }

    @Test
    void testFindByMemberAndConcert() {
        // Given
        Member testMember = memberRepository.findAll().get(0);
        Concert testConcert = concertRepository.findAll().get(0);

        // When
        Optional<ConcertLike> concertLike = concertLikeRepository.findByMemberAndConcert(testMember, testConcert);

        // Then
        assertThat(concertLike).isPresent();
        assertThat(concertLike.get().getMember().getId()).isEqualTo(testMember.getId());
        assertThat(concertLike.get().getConcert().getId()).isEqualTo(testConcert.getId());
    }

    @Test
    void testExistsByConcertIdAndMemberId() {
        // Given
        Member testMember = memberRepository.findAll().get(0);
        Concert testConcert = concertRepository.findAll().get(0);

        // When
        boolean exists = concertLikeRepository.existsByConcertIdAndMemberId(testConcert.getId(), testMember.getId());

        // Then
        assertThat(exists).isTrue();
    }



}
