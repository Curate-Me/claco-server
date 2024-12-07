package com.curateme.claco.concert.domain.dto.request;

import com.curateme.claco.concert.domain.entity.Concert;
import com.curateme.claco.concert.domain.entity.ConcertLike;
import com.curateme.claco.member.domain.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConcertLikesRequest {

    @Schema(description = "멤버 아이디")
    private Long memberId;

    @Schema(description = "공연 아이디")
    private Long concertId;

    public ConcertLike toEntity(Member member, Concert concert) {
        return ConcertLike.builder()
            .member(member)
            .concert(concert)
            .build();
    }
}
