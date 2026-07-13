package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.LoginRequest;
import com.fashionshop.dto.UserRegisterDTO;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.security.JwtUtils;
import com.fashionshop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String token = jwtUtils.generateToken(authentication.getName());
            return ApiResponse.success(userService.generateLoginResponse(authentication.getName(), token));

        } catch (AuthenticationException e) {
            throw new FashionShopException(ErrorCode.UNAUTHENTICATED);
        }
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@Valid @RequestBody UserRegisterDTO request) {
        userService.registerUser(request);
        return ApiResponse.success("Ä\ufffdÄƒng kÃ½ thÃ nh cÃ´ng!");
    }
}
