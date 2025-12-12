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
            .csrf(csrf -> csrf.disable()) // Tắt CSRF để test dễ dàng
            .authorizeHttpRequests(auth -> auth
                
                // 1. CHO PHÉP TÀI NGUYÊN TĨNH (CSS, JS, ẢNH)
                // Của Admin
                .requestMatchers("/admin/css/**", "/admin/js/**", "/admin/vendor/**", "/admin/img/**", "/favicon.ico").permitAll()
                // Của Client (Sau này bạn thêm vào)
                .requestMatchers("/client/css/**", "/client/js/**", "/client/img/**").permitAll()
                .requestMatchers("/uploads/**").permitAll() // Cho phép xem ảnh sản phẩm upload lên
                .requestMatchers("/new-arrival/**", "/danh-muc/**").permitAll()
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
                    .loginPage("/login")  // 1. Dùng trang login của mình (ko dùng mặc định)
                    .loginProcessingUrl("/login") // 2. URL để form submit POST vào (Spring Security tự xử lý)
//                    .defaultSuccessUrl("/", true) // 3. Đăng nhập xong thì về trang chủ
                    .successHandler(customSuccessHandler)
                    .failureUrl("/login?error=true") // 4. Sai mật khẩu thì về lại login kèm lỗi
 
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