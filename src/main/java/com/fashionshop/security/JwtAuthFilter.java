package com.fashionshop.security;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

import com.fashionshop.service.impl.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String userEmail;
        final String jwtToken;

        // Nếu không có header Authorization hoặc không bắt đầu bằng Bearer thì bỏ qua
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        try {
            userEmail = jwtUtils.extractEmail(jwtToken);
            
            // Nếu có email và chưa được xác thực
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                
                // Chặn tài khoản đã bị vô hiệu hóa (Banned)
                if (!userDetails.isEnabled()) {
                    throw new FashionShopException(ErrorCode.UNAUTHORIZED, "Tài khoản đã bị khóa hoặc vô hiệu hóa");
                }
                
                // Token đã được verify chữ ký và hạn sử dụng thành công ở hàm extractEmail (JJWT tự động ném lỗi nếu sai)
                // Không cần gọi isTokenValid để parse lại Token thêm 2 lần nữa (Gây tốn CPU, rủi ro DoS)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Cập nhật SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Log lỗi nếu token sai hoặc hết hạn
            logger.error("Lỗi xác thực JWT Token: " + e.getMessage());
            
            // QUAN TRỌNG: Nếu user đã gửi Token nhưng Token sai/hết hạn, 
            // KHÔNG cho phép đi tiếp dưới danh nghĩa Khách (Guest) để tránh lỗi logic/bảo mật.
            // Trả về 401 ngay lập tức!
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\": 401, \"message\": \"Token không hợp lệ hoặc đã hết hạn\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
