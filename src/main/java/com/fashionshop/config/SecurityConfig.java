package com.fashionshop.config;

import com.fashionshop.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomSuccessHandler customSuccessHandler;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    // Cáº§n cung cáº¥p AuthenticationManager cho AuthController sá»­ dá»¥ng Ä‘á»ƒ Ä‘Äƒng nháº­p
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 1. Cáº¥u hÃ¬nh báº£o máº­t riÃªng cho REST API (Æ¯u tiÃªn sá»‘ 1)
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**") // Chá»‰ báº¯t cÃ¡c request cÃ³ tiá»\ufffdn tá»‘ /api
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT khÃ´ng dÃ¹ng Session
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll() // Cho phÃ©p Ä\ufffdÄƒng nháº­p/Ä\ufffdÄƒng kÃ½
                .requestMatchers("/api/v1/home", "/api/v1/products/**", "/api/v1/cart/**", "/api/v1/checkout/**").permitAll() // CÃ¡c API public
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"code\": 401, \"message\": \"ChÆ°a Ä‘Äƒng nháº­p hoáº·c phiÃªn Ä‘Äƒng nháº­p háº¿t háº¡n\"}");
                })
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // ThÃªm bá»™ lá»\ufffdc kiá»ƒm tra JWT

        return http.build();
    }

    // 2. Cáº¥u hÃ¬nh báº£o máº­t cho MVC / Admin cÅ© (Æ¯u tiÃªn sá»‘ 2)
    @Bean
    @Order(2)
    public SecurityFilterChain mvcSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                
                .requestMatchers("/admin/css/**", "/admin/js/**", "/admin/vendor/**", "/admin/img/**", "/favicon.ico", "/favicon.png").permitAll()
                .requestMatchers("/client/css/**", "/client/js/**", "/client/img/**", "/uploads/**").permitAll()

                .requestMatchers("/", "/home", "/login", "/register", "/contact").permitAll()
                
                .requestMatchers("/danh-muc/**", "/san-pham/**", "/search", "/new-arrival/**","/product/**", "/pages/**").permitAll()
                
                .requestMatchers("/cart/**", "/checkout/**", "/order/**", "/api/wishlist/**").permitAll()

                .requestMatchers("/admin","/admin/**").hasRole("ADMIN")

                .requestMatchers("/account/**").authenticated()

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