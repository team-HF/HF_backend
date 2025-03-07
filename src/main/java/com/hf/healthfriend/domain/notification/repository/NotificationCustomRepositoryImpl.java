package com.hf.healthfriend.domain.notification.repository;

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
    public List<NotificationResponse> getList(NotificationType notificationType, Pageable pageable) {
        BooleanBuilder builder = filter(notificationType);
        return queryFactory
                .selectFrom(notification)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream().map(notification-> NotificationResponse.builder()
                        .message(notification.getMessage())
                        .type(notification.getType()) // 이거 notificationType으로 써야됨
                        .time(notification.getCreationTime())
                        .targetId(notification.getTargetId())
                        .build()).toList();
    }

    public BooleanBuilder filter(NotificationType notificationType) {
        BooleanBuilder builder = new BooleanBuilder();
        if (notificationType != null) {
            builder.and(notification.type.eq(notificationType));
        }
        return builder;
    }


}
