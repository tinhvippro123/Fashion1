package com.fashionshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF
            .authorizeHttpRequests(auth -> auth
                
                // 1. TÀI NGUYÊN TĨNH (Cho phép hết)
                .requestMatchers("/admin/css/**", "/admin/js/**", "/admin/vendor/**", "/admin/img/**", "/favicon.ico").permitAll()
                .requestMatchers("/client/css/**", "/client/js/**", "/client/img/**", "/uploads/**").permitAll()

                // 2. TRANG CÔNG KHAI (KHÁCH VÃNG LAI XEM ĐƯỢC)
                .requestMatchers("/", "/home", "/login", "/register").permitAll()
                
                // --- QUAN TRỌNG: CÁC TRANG DANH MỤC, SẢN PHẨM, TÌM KIẾM ---
                // Thêm /tim-kiem vào đây để fix lỗi search bị bắt đăng nhập
                .requestMatchers("/danh-muc/**", "/san-pham/**", "/search", "/new-arrival/**","/product/**").permitAll()
                
                // --- GIỎ HÀNG & THANH TOÁN & TRA CỨU ĐƠN ---
                // Cho phép khách đặt hàng và tra cứu đơn không cần login
                .requestMatchers("/cart/**", "/checkout/**", "/order/**").permitAll()

                // 3. TRANG QUẢN TRỊ (ADMIN)
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // 4. TRANG CÁ NHÂN (USER - Cần đăng nhập)
                // Các trang như: Đổi mật khẩu, Lịch sử mua hàng, Sổ địa chỉ
                .requestMatchers("/account/**").authenticated()

                // 5. CÁC TRANG KHÁC -> BẮT BUỘC ĐĂNG NHẬP
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}