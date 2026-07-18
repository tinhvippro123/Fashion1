package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getStats() {
        // Mocked stats for now. In reality, we'd query repositories for counts/sums.
        return ApiResponse.success(Map.of(
            "totalRevenue", 150000000,
            "totalOrders", 150,
            "totalProducts", 450,
            "totalUsers", 1200
        ));
    }
}
