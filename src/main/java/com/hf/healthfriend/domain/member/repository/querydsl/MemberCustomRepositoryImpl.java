package com.hf.healthfriend.domain.member.repository.querydsl;

import static com.querydsl.core.types.ExpressionUtils.count;


import com.hf.healthfriend.domain.member.constant.CompanionStyle;
import com.hf.healthfriend.domain.member.constant.FitnessEagerness;
import com.hf.healthfriend.domain.member.constant.FitnessKind;
import com.hf.healthfriend.domain.member.constant.FitnessLevel;
import com.hf.healthfriend.domain.member.constant.FitnessObjective;
import com.hf.healthfriend.domain.member.constant.MemberSortType;
import com.hf.healthfriend.domain.member.dto.request.MembersSearchRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberRecommendResponse;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.entity.QMember;
import com.hf.healthfriend.domain.spec.entity.QSpec;
import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.member.repository.dto.ProfileQueryResultDto;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.wish.entity.QWish;
import com.hf.healthfriend.global.util.mapping.BeanMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private final QMember member = QMember.member;
    private final QSpec spec = QSpec.spec;
    private final QWish wish = QWish.wish;
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;
    private final BeanMapper beanMapper;

    @Override
    public List<MemberSearchResponse> searchMembers (String keyword, MembersSearchRequest request, Pageable pageable) {
        BooleanBuilder builder = filter(keyword, request);
        OrderSpecifier<?>[] orderSpecifier = getSortType(request);
        return queryFactory
                .select(Projections.constructor(MemberSearchResponse.class,
                        member.profileImageUrl,
                        member.introduction,
                        member.nickname,
                        member.wishedCount,
                        member.matchedCount,
                        member.fitnessLevel.stringValue(),
                        member.companionStyle.stringValue(),
                        member.fitnessEagerness.stringValue(),
                        member.fitnessKind.stringValue(),
                        member.fitnessObjective.stringValue()))
                .from(member)
                .where(builder)
                .groupBy(member)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public OrderSpecifier<?>[] getSortType(MembersSearchRequest request) {
        MemberSortType sortType = request.getMemberSortType();
        if (sortType==null) return new OrderSpecifier<?>[]{member.matchedCount.desc()};
        switch(sortType){
            case SCORE -> {
                return new OrderSpecifier<?>[]{member.reviewScore.desc()};
            }
            case WISH_COUNT -> {
                return new OrderSpecifier<?>[]{member.wishedCount.desc()};
            }
            // TODO : 채팅 기능이 구현되면 이어서 작업
            case RESPONSE_RATE -> {
                return new OrderSpecifier<?>[]{};
            }
            default -> {
                return new OrderSpecifier<?>[]{member.matchedCount.desc()};
            }
        }
    }

    public BooleanBuilder filter(String keyword, MembersSearchRequest request) {
        BooleanBuilder builder = new BooleanBuilder();
        if(request.getFitnessLevel()!=null)
            builder.and(member.fitnessLevel.eq(FitnessLevel.valueOf(request.getFitnessLevel())));
        if(request.getCompanionStyle()!=null)
            builder.and(member.companionStyle.eq(CompanionStyle.valueOf(request.getCompanionStyle())));
        if(request.getFitnessEagerness()!=null)
            builder.and(member.fitnessEagerness.eq(FitnessEagerness.valueOf(request.getFitnessEagerness())));
        if(request.getFitnessKind()!=null)
            builder.and(member.fitnessKind.eq(FitnessKind.valueOf(request.getFitnessKind())));
        if(request.getFitnessObjective()!=null)
            builder.and(member.fitnessObjective.eq(FitnessObjective.valueOf(request.getFitnessObjective())));
        if(request.getCd1()!=null)
            builder.and(member.cd1.eq(request.getCd1()));
        if(request.getCd2()!=null)
            builder.and(member.cd2.eq(request.getCd2()));
        if(request.getCd3()!=null)
            builder.and(member.cd3.eq(request.getCd3()));
        if(keyword!=null)
                builder.and(member.introduction.containsIgnoreCase(keyword)
                        .or(member.nickname.containsIgnoreCase(keyword)));
        return builder;
    }

    @Override
    public Optional<Member> findByMemberId(Long memberId) {
        Member findMember = this.queryFactory.selectFrom(this.member)
                .leftJoin(this.spec).on(this.spec.member.id.eq(this.member.id))
                .where(this.spec.isDeleted.isNull().or(this.spec.isDeleted.isFalse()), this.member.id.eq(memberId), this.member.isDeleted.isFalse())
                .fetchOne();
        return Optional.ofNullable(findMember);
    }

    @Override
    public Member update(Long memberId, MemberUpdateDto updateDto) {
        Member member = this.em.find(Member.class, memberId);
        if (member == null) {
            throw new MemberNotFoundException(memberId);
        }

        this.beanMapper.copyProperties(updateDto, member);

        return member;
    }

    public Optional<ProfileQueryResultDto> findProfileByMemberId(Long memberId) {
        List<ProfileQueryResultDto> result = this.queryFactory.selectFrom(this.member)
                .leftJoin(this.spec).on(this.spec.member.eq(this.member))
                .where(this.member.id.eq(memberId),
                        this.member.isDeleted.isFalse(),
                        this.spec.isDeleted.isFalse().or(this.spec.isNull()))
                .orderBy(this.spec.startDate.desc(), this.spec.endDate.desc().nullsFirst())
                .transform(GroupBy.groupBy(this.member.id).list(
                        Projections.constructor(
                                ProfileQueryResultDto.class,
                                this.member.id,
                                this.member.introduction,
                                GroupBy.list(
                                        Projections.constructor(
                                                SpecDto.class,
                                                this.spec.specId,
                                                this.spec.startDate,
                                                this.spec.endDate,
                                                this.spec.isCurrent,
                                                this.spec.title,
                                                this.spec.description
                                        )
                                ),
                                this.member.reviewScore
                        )
                ));
        if (result.size() > 1) {
            log.warn("findProfileByMemberId - 쿼리 결과 리스트의 사이즈가 1을 초과합니다.");
            result.forEach((r) -> log.warn("memberId={}", r.memberId()));
        }

        return result.isEmpty() ? Optional.empty() : Optional.of(removeNull(result.get(0)));
    }

    private ProfileQueryResultDto removeNull(ProfileQueryResultDto dto) {
        for (int i = 0; i < dto.specs().size(); i++) {
            SpecDto specDto = dto.specs().get(i);
            if (specDto.getSpecId() == null) {
                dto.specs().remove(i--);
            }
        }
        return dto;
    }
}
