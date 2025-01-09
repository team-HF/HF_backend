package com.hf.healthfriend.domain.member.repository.querydsl;

import static com.querydsl.core.types.ExpressionUtils.count;


import com.hf.healthfriend.domain.member.constant.MemberSortType;
import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberRecommendResponse;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.entity.QMember;
import com.hf.healthfriend.domain.spec.entity.QSpec;
import com.hf.healthfriend.domain.member.dto.response.MemberSearchResponse;
import com.hf.healthfriend.domain.member.entity.Member;
import com.hf.healthfriend.domain.member.entity.QMember;
import com.hf.healthfriend.domain.member.exception.MemberNotFoundException;
import com.hf.healthfriend.domain.member.repository.dto.MemberUpdateDto;
import com.hf.healthfriend.domain.member.repository.dto.ProfileQueryResultDto;
import com.hf.healthfriend.domain.spec.dto.SpecDto;
import com.hf.healthfriend.domain.spec.entity.QSpec;
import com.hf.healthfriend.domain.wish.entity.QWish;
import com.hf.healthfriend.global.util.mapping.BeanMapper;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.ExpressionUtils;
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

import static com.querydsl.core.types.ExpressionUtils.count;

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
    public List<MemberRecommendResponse> recommendMembers(MembersRecommendRequest request, Pageable pageable) {
        BooleanBuilder builder = filter(request);
        List<String> fitnessTypeList = fitnessTypesToList(request); // fitnessTypeList 업데이트는 별도의 메서드로 분리
        OrderSpecifier<?>[] orderSpecifier = getSortType(request);
        List<MemberRecommendResponse> result = queryFactory
                .select(Projections.constructor(MemberRecommendResponse.class,
                        member.profileImageUrl,
                        member.nickname,
                        member.matchedCount,
                        member.introduction))
                .from(member)
                .where(builder)
                .groupBy(member)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return result.stream()
                .map(dto -> new MemberRecommendResponse(
                        dto.profileImageUrl(),
                        dto.nickName(),
                        dto.matchingCount(),
                        dto.introduction(),
                        fitnessTypeList))
                .toList();
    }

    @Override
    public List<MemberSearchResponse> searchMembers(String keyword, Pageable pageable) {
        BooleanBuilder builder = searchFilter(keyword);
        return queryFactory
                .select(Projections.bean(MemberSearchResponse.class,
                        member.profileImageUrl,
                        member.introduction,
                        member.nickname,
                        member.wishedCount,
                        member.matchedCount,
                        member.companionStyle,
                        member.fitnessEagerness,
                        member.fitnessKind,
                        member.fitnessObjective
                        ))
                .from(member)
                .where(builder)
                .groupBy(member)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public OrderSpecifier<?>[] getSortType(MembersRecommendRequest request) {
        MemberSortType sortType = request.getMemberSortType();
        if (sortType==null) return new OrderSpecifier<?>[]{member.matchedCount.desc()};
        switch(sortType){
            case SCORE -> {
                return new OrderSpecifier<?>[]{member.reviewScore.desc()};
            }
            case WISH_COUNT -> {
                return new OrderSpecifier<?>[]{
                        new OrderSpecifier<>(Order.DESC,
                                JPAExpressions
                                        .select(count(wish.wisher))
                                        .from(wish)
                                        .where(wish.wished.id.eq(member.id))
                        )
                };
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

    public BooleanBuilder filter(MembersRecommendRequest request) {
        BooleanBuilder builder = new BooleanBuilder();

        addConditionsToBuilder(builder, request.getCompanionStyleList(), member.companionStyle::eq);
        addConditionsToBuilder(builder, request.getFitnessEagernessList(), member.fitnessEagerness::eq);
        addConditionsToBuilder(builder, request.getFitnessKindList(), member.fitnessKind::eq);
        addConditionsToBuilder(builder, request.getFitnessObjectiveList(), member.fitnessObjective::eq);

        return builder;
    }

    public List<String> fitnessTypesToList(MembersRecommendRequest request) {
        List<String> fitnessTypeList = new ArrayList<>();

        addTypesToList(fitnessTypeList, request.getCompanionStyleList());
        addTypesToList(fitnessTypeList, request.getFitnessEagernessList());
        addTypesToList(fitnessTypeList, request.getFitnessKindList());
        addTypesToList(fitnessTypeList, request.getFitnessObjectiveList());

        return fitnessTypeList;
    }

    // 제너릭 메서드
    private <T> void addConditionsToBuilder(BooleanBuilder builder, List<T> items, Function<T, BooleanExpression> expression) {
        if (items != null)
            for (T item : items)
                builder.and(expression.apply(item));
    }

    private <T extends Enum<T>> void addTypesToList(List<String> fitnessTypeList, List<T> items) {
        if (items != null)
            for (T item : items)
                fitnessTypeList.add(item.name());
    }

    @Override
    public Optional<Member> findByMemberId(Long memberId) {
        Member findMember = this.queryFactory.selectFrom(this.member)
                .leftJoin(this.spec).on(this.spec.member.id.eq(this.member.id))
                .where(this.spec.isDeleted.isNull().or(this.spec.isDeleted.isFalse()), this.member.id.eq(memberId), this.member.isDeleted.isFalse())
                .fetchOne();
        return Optional.ofNullable(findMember);
    }

    public BooleanBuilder searchFilter(String keyword) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(member.introduction.containsIgnoreCase(keyword)
                .or(member.nickname.containsIgnoreCase(keyword)));
        return builder;
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
