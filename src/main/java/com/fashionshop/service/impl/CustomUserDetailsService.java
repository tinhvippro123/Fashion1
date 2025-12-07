package com.fashionshop.service.impl;

import com.fashionshop.model.User;
import com.fashionshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Tìm user trong DB bằng email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // 2. Chuyển đổi sang UserDetails của Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash()) // Lấy mật khẩu đã mã hóa từ DB
                .roles(user.getRole().name()) // Set Role (Ví dụ: "ADMIN", "USER")
                .disabled(!user.getIsActive()) // Kiểm tra active
                .build();
    }
}