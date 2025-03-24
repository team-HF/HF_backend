package com.hf.healthfriend.domain.notification.controller;

import com.hf.healthfriend.domain.notification.constant.NotificationType;
import com.hf.healthfriend.domain.notification.dto.NotificationEvent;
import com.hf.healthfriend.domain.notification.dto.NotificationListResponse;
import com.hf.healthfriend.domain.notification.dto.NotificationResponse;
import com.hf.healthfriend.domain.notification.service.NotificationService;
import com.hf.healthfriend.global.spec.ApiBasicResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Notification API", description = "알람 API")
@RequiredArgsConstructor
@RequestMapping("/hf")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알람 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "알람 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "알람 목록 조회 실패")
    })
    @GetMapping("/notification/list")
    public ResponseEntity<ApiBasicResponse<NotificationListResponse>> getList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam int size,
            @RequestParam @Nullable NotificationType notificationType) {
        return ResponseEntity.ok(ApiBasicResponse.of(notificationService.getList(page,size,notificationType),
                HttpStatus.OK));
    }

    private final ApplicationEventPublisher eventPublisher;
    // ✅ **성능 테스트용 API 추가**
    @Operation(summary = "알람 테스트 (직접 요청)", responses = {
            @ApiResponse(responseCode = "200", description = "알람 요청 성공"),
            @ApiResponse(responseCode = "400", description = "알람 요청 실패")
    })
    @PostMapping("/notification/test")
    @Transactional
    public ResponseEntity<ApiBasicResponse<String>> testNotification(
            @RequestParam Long memberId,
            @RequestParam NotificationType type,
            @RequestParam String actor,
            @RequestParam Long targetId) {

        NotificationEvent event = new NotificationEvent(memberId, type, actor, targetId);

        // ✅ 트랜잭션 없이 직접 이벤트 발행
        eventPublisher.publishEvent(event);

        return ResponseEntity.ok(ApiBasicResponse.of("알림 요청 성공", HttpStatus.OK));
    }
}
