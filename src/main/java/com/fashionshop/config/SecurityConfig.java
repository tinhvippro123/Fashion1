package com.fashionshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF để test dễ dàng
            .authorizeHttpRequests(auth -> auth
                
                // 1. CHO PHÉP TÀI NGUYÊN TĨNH (CSS, JS, ẢNH)
                // Của Admin
                .requestMatchers("/admin/css/**", "/admin/js/**", "/admin/vendor/**", "/admin/img/**").permitAll()
                // Của Client (Sau này bạn thêm vào)
                .requestMatchers("/client/css/**", "/client/js/**", "/client/img/**").permitAll()
                .requestMatchers("/uploads/**").permitAll() // Cho phép xem ảnh sản phẩm upload lên

                // 2. PHÂN QUYỀN TRANG ADMIN (Bắt buộc đăng nhập & có quyền ADMIN)
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 3. PHÂN QUYỀN TRANG CLIENT (Cho phép tất cả mọi người truy cập)
                // Trang chủ, Xem sản phẩm, Giỏ hàng -> Không cần đăng nhập
                .requestMatchers("/", "/home", "/cart/**", "/checkout/**", "/order/**").permitAll()
                
                // Trang đăng ký, đăng nhập
                .requestMatchers("/login", "/register").permitAll()
                .requestMatchers("/product/**").permitAll()
                // Các trang còn lại (Ví dụ: Trang cá nhân /profile) -> Phải đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // .loginPage("/login") // Khi nào làm giao diện login client thì mở
                .defaultSuccessUrl("/admin/users", true) // Tạm thời login xong vào Admin
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}