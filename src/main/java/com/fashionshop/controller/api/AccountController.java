package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.ChangePasswordRequest;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.model.User;
import com.fashionshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fashionshop.dto.UpdateProfileRequest;
import com.fashionshop.dto.UserProfileResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController("apiAccountController")
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ApiResponse<Map<String, Object>> getProfile(Principal principal) {
        User user = userService.getUserByEmailOrThrow(principal.getName());
        
        UserProfileResponse profileResponse = new UserProfileResponse(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", profileResponse);

        return ApiResponse.success(response);
    }

    @PutMapping("/update")
    public ApiResponse<String> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Principal principal) {
            
        User user = userService.getUserByEmailOrThrow(principal.getName());
        userService.updateProfile(user, request.getEmail(), request.getGender());
        return ApiResponse.success("Cập nhật thông tin thành công!");
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Principal principal) {
            
        User user = userService.getUserByEmailOrThrow(principal.getName());
        userService.changePassword(user, request.getCurrentPassword(), request.getNewPassword(), request.getConfirmPassword());
        return ApiResponse.success("Đổi mật khẩu thành công!");
    }
}
