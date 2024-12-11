package com.hf.healthfriend.domain.notification.repository;

import com.hf.healthfriend.domain.notification.constant.NotificationGetType;
import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.dto.NotificationResponse;
import com.hf.healthfriend.domain.notification.entity.QNotification;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationCustomRepositoryImpl implements NotificationCustomRepository {
    private final JPAQueryFactory queryFactory;
    private final QNotification notification = QNotification.notification;

    @Override
    public List<NotificationResponse> getList(NotificationGetType notificationGetType, Pageable pageable) {
        BooleanBuilder builder = filter(notificationGetType);
        return queryFactory
                .selectFrom(notification)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream().map(notification-> NotificationResponse.builder()
                        .message(notification.getMessage())
                        .type(notificationGetType)
                        .targetId(notification.getTargetId())
                        .build()).toList();
    }

    public BooleanBuilder filter(NotificationGetType notificationGetType) {
        BooleanBuilder builder = new BooleanBuilder();
        switch (notificationGetType) {
            case COMMUNITY -> builder.and(
                    notification.type.eq(NotificationType.ADD_COMMENT_TO_POST)
                            .or(notification.type.eq(NotificationType.ADD_COMMENT_TO_COMMENT))
                            .or(notification.type.eq(NotificationType.ADD_LIKE_TO_POST))
                            .or(notification.type.eq(NotificationType.ADD_LIKE_TO_COMMENT))
            );
            case REVIEW -> builder.and(notification.type.eq(NotificationType.MATCH_END_REVIEW));
            case MATCHING -> builder.and(
                    notification.type.eq(NotificationType.MATCH_ACCEPT)
                            .or(notification.type.eq(NotificationType.MATCH_REJECT))
                            .or(notification.type.eq(NotificationType.MATCH_REQUEST))
                            .or(notification.type.eq(NotificationType.MATCH_PUNK))
            );
        }
        return builder;
    }


}
