package com.fashionshop.config;
import com.fashionshop.service.LoginHistoryService;

import com.fashionshop.model.User;
import com.fashionshop.service.CartService;
import com.fashionshop.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private CartService cartService; // Cáº§n cÃ¡i nÃ y Ä‘á»ƒ gá»™p giá»\ufffd

    @Autowired
    private UserService userService; // Cáº§n cÃ¡i nÃ y Ä‘á»ƒ tÃ¬m User

    @Autowired
    private LoginHistoryService loginHistoryService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        // --- PHáº¦N 1: Xá»¬ LÃ\ufffd Gá»˜P GIá»Ž HÃ€NG (Má»šI THÃŠM) ---
//        try {
//            // 1. Láº¥y User vá»«a Ä‘Äƒng nháº­p
//            String email = authentication.getName();
//            User user = userService.findByEmail(email);
//
//            // 2. Láº¥y Session ID (Giá»\ufffd hÃ ng khÃ¡ch vÃ£ng lai)
//            HttpSession session = request.getSession();
//            String sessionId = (String) session.getAttribute("CART_SESSION_ID");
//
//            // 3. Náº¿u cÃ³ giá»\ufffd hÃ ng cÅ© -> Gá»\ufffdi hÃ m gá»™p
//            if (sessionId != null && user != null) {
//                cartService.mergeCart(sessionId, user);
//                
//                // XÃ³a session ID cÅ© Ä‘i vÃ¬ giá»\ufffd Ä‘Ã£ lÆ°u vÃ o DB cá»§a user rá»“i
//                session.removeAttribute("CART_SESSION_ID"); 
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Lá»—i khi gá»™p giá»\ufffd hÃ ng: " + e.getMessage());
//            // KhÃ´ng throw exception Ä‘á»ƒ váº«n cho user Ä‘Äƒng nháº­p bÃ¬nh thÆ°á»\ufffdng dÃ¹ lá»—i giá»\ufffd hÃ ng
//        }
    	
    	
    	System.out.println("====== Báº®T Ä\ufffdáº¦U LOGIN SUCCESS ======"); // LOG 1

        try {
            User user = userService.findByEmail(authentication.getName());
            HttpSession session = request.getSession();
            String sessionId = (String) session.getAttribute("CART_SESSION_ID");

            System.out.println("User Email: " + user.getEmail()); // LOG 2
            System.out.println("Session ID tÃ¬m tháº¥y: " + sessionId); // LOG 3

            if (sessionId != null && user != null) {
                System.out.println(">>> Ä\ufffdANG Gá»ŒI HÃ€M MERGE CART..."); // LOG 4
                cartService.mergeCart(sessionId, user);
                session.removeAttribute("CART_SESSION_ID"); 
            } else {
                System.out.println(">>> KHÃ”NG Gá»˜P: Do SessionId null hoáº·c User null"); // LOG 5
            }
            
            // Ghi nháº­n lá»‹ch sá»­ Ä‘Äƒng nháº­p
            if (user != null) {
                loginHistoryService.saveLoginHistory(user, request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
    	
    	

        // --- PHáº¦N 2: CHUYá»‚N HÆ¯á»šNG THEO ROLE (CODE CÅ¨ Cá»¦A Báº N) ---
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String redirectUrl = request.getContextPath();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/users"; 
                break;
            } else if (authority.getAuthority().equals("ROLE_USER")) {
                redirectUrl = "/";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}