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

        // Náº¿u khÃ´ng cÃ³ header Authorization hoáº·c khÃ´ng báº¯t Ä‘áº§u báº±ng Bearer thÃ¬ bá»\ufffd qua
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        try {
            userEmail = jwtUtils.extractEmail(jwtToken);
            
            // Náº¿u cÃ³ email vÃ  chÆ°a Ä‘Æ°á»£c xÃ¡c thá»±c
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                
                // Cháº·n tÃ i khoáº£n Ä‘Ã£ bá»‹ vÃ´ hiá»‡u hÃ³a (Banned)
                if (!userDetails.isEnabled()) {
                    throw new FashionShopException(ErrorCode.UNAUTHORIZED, "TÃ i khoáº£n Ä‘Ã£ bá»‹ khÃ³a hoáº·c vÃ´ hiá»‡u hÃ³a");
                }
                
                // Token Ä‘Ã£ Ä‘Æ°á»£c verify chá»¯ kÃ½ vÃ  háº¡n sá»­ dá»¥ng thÃ nh cÃ´ng á»Ÿ hÃ m extractEmail (JJWT tá»± Ä‘á»™ng nÃ©m lá»—i náº¿u sai)
                // KhÃ´ng cáº§n gá»\ufffdi isTokenValid Ä‘á»ƒ parse láº¡i Token thÃªm 2 láº§n ná»¯a (GÃ¢y tá»‘n CPU, rá»§i ro DoS)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Cáº­p nháº­t SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Log lá»—i náº¿u token sai hoáº·c háº¿t háº¡n
            logger.error("Lá»—i xÃ¡c thá»±c JWT Token: " + e.getMessage());
            
            // QUAN TRá»ŒNG: Náº¿u user Ä‘Ã£ gá»­i Token nhÆ°ng Token sai/háº¿t háº¡n, 
            // KHÃ”NG cho phÃ©p Ä‘i tiáº¿p dÆ°á»›i danh nghÄ©a KhÃ¡ch (Guest) Ä‘á»ƒ trÃ¡nh lá»—i logic/báº£o máº­t.
            // Tráº£ vá»\ufffd 401 ngay láº­p tá»©c!
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\": 401, \"message\": \"Token khÃ´ng há»£p lá»‡ hoáº·c Ä‘Ã£ háº¿t háº¡n\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
