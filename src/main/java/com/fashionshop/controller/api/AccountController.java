package com.fashionshop.controller.api;



import com.fashionshop.dto.ApiResponse;

import com.fashionshop.dto.ChangePasswordRequest;

import com.fashionshop.exception.ErrorCode;

import com.fashionshop.exception.FashionShopException;

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

        return ApiResponse.success(userService.getProfileData(principal.getName()));

    }



    @PutMapping("/update")

    public ApiResponse<String> updateProfile(

            @Valid @RequestBody UpdateProfileRequest request,

            Principal principal) {

            

        userService.updateProfileByEmail(principal.getName(), request.getEmail(), request.getGender());

        return ApiResponse.success("Cập nhật thông tin thành công!");

    }



    @PutMapping("/change-password")

    public ApiResponse<String> changePassword(

            @Valid @RequestBody ChangePasswordRequest request,

            Principal principal) {

            

        userService.changePasswordByEmail(principal.getName(), request.getCurrentPassword(), request.getNewPassword(), request.getConfirmPassword());

        return ApiResponse.success("Đổi mật khẩu thành công!");

    }

}

