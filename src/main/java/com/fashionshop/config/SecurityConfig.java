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
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                
                .requestMatchers("/admin/css/**", "/admin/js/**", "/admin/vendor/**", "/admin/img/**", "/favicon.ico").permitAll()
                .requestMatchers("/client/css/**", "/client/js/**", "/client/img/**", "/uploads/**").permitAll()

                .requestMatchers("/", "/home", "/login", "/register").permitAll()
                
                .requestMatchers("/danh-muc/**", "/san-pham/**", "/search", "/new-arrival/**","/product/**").permitAll()
                
                .requestMatchers("/cart/**", "/checkout/**", "/order/**").permitAll()

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