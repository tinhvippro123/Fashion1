package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.Notification;
import com.fashionshop.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/notifications")
public class AdminNotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @GetMapping("/latest")
    public ApiResponse<Map<String, Object>> getLatestNotifications() {
        List<Notification> notifications = notificationRepository.findTop5ByOrderByCreatedAtDesc();
        long unreadCount = notificationRepository.countByIsReadFalse();
        
        return ApiResponse.success(Map.of(
            "notifications", notifications,
            "unreadCount", unreadCount
        ));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<String> markAsRead(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
        return ApiResponse.success("Đã đánh dấu đọc");
    }
}
