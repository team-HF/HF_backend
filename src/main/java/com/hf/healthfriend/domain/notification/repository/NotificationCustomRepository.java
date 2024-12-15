package com.hf.healthfriend.domain.notification.repository;

import com.hf.healthfriend.domain.notification.constant.NotificationGetType;
import com.hf.healthfriend.domain.notification.dto.NotificationResponse;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface NotificationCustomRepository {
    List<NotificationResponse> getList(NotificationGetType notificationGetType, Pageable pageable);
}
