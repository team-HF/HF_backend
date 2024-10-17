package com.hf.healthfriend.domain.member.repository.querydsl;


import com.hf.healthfriend.domain.member.constant.*;
import com.hf.healthfriend.domain.member.dto.request.MembersRecommendRequest;
import com.hf.healthfriend.domain.member.dto.response.MemberRecommendResponse;
import com.hf.healthfriend.domain.member.entity.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class MemberCustomRepositoryImpl implements MemberCustomRepository {
    private final QMember member = QMember.member;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberRecommendResponse> recommendMembers(MembersRecommendRequest request, Pageable pageable) {
        BooleanBuilder builder = filter(request);
        List<String> fitnessTypeList = fitnessTypesToList(request); // fitnessTypeList 업데이트는 별도의 메서드로 분리
        OrderSpecifier<?>[] orderSpecifier = getSortType(request);
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .groupBy(member)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream().map(member -> MemberRecommendResponse.builder()
                        .profileImageUrl(member.getProfileImageUrl())
                        .introduction(member.getIntroduction())
                        .matchingCount(member.getMatchedCount())
                        .nickName(member.getNickname())
                        .fitnessType(fitnessTypeList)
                        .build()).toList();
    }

    public OrderSpecifier<?>[] getSortType(MembersRecommendRequest request) {
        MemberSortType sortType = request.getMemberSortType();
        if (sortType==null) return new OrderSpecifier<?>[]{member.matchedCount.desc()};
        switch(sortType){
            case SCORE -> {
                return new OrderSpecifier<?>[]{member.reviewScore.desc()};
            }
            // TODO : 찜하기 기능이 구현되면 이어서 작업
            case HEART_COUNT -> {
                return new OrderSpecifier<?>[]{};
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
}
