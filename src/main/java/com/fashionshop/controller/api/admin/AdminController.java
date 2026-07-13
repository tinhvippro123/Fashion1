package com.fashionshop.controller.api.admin;
import com.fashionshop.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/status")
    public ApiResponse<Map<String, String>> status() {
        return ApiResponse.success(Map.of("status", "System Online", "version", "1.0.0"));
    }
}
