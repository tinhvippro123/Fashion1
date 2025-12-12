package com.fashionshop.config;

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
    private CartService cartService; // Cần cái này để gộp giỏ

    @Autowired
    private UserService userService; // Cần cái này để tìm User

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException, ServletException {
        
        // --- PHẦN 1: XỬ LÝ GỘP GIỎ HÀNG (MỚI THÊM) ---
//        try {
//            // 1. Lấy User vừa đăng nhập
//            String email = authentication.getName();
//            User user = userService.findByEmail(email);
//
//            // 2. Lấy Session ID (Giỏ hàng khách vãng lai)
//            HttpSession session = request.getSession();
//            String sessionId = (String) session.getAttribute("CART_SESSION_ID");
//
//            // 3. Nếu có giỏ hàng cũ -> Gọi hàm gộp
//            if (sessionId != null && user != null) {
//                cartService.mergeCart(sessionId, user);
//                
//                // Xóa session ID cũ đi vì giờ đã lưu vào DB của user rồi
//                session.removeAttribute("CART_SESSION_ID"); 
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("Lỗi khi gộp giỏ hàng: " + e.getMessage());
//            // Không throw exception để vẫn cho user đăng nhập bình thường dù lỗi giỏ hàng
//        }
    	
    	
    	System.out.println("====== BẮT ĐẦU LOGIN SUCCESS ======"); // LOG 1

        try {
            User user = userService.findByEmail(authentication.getName());
            HttpSession session = request.getSession();
            String sessionId = (String) session.getAttribute("CART_SESSION_ID");

            System.out.println("User Email: " + user.getEmail()); // LOG 2
            System.out.println("Session ID tìm thấy: " + sessionId); // LOG 3

            if (sessionId != null && user != null) {
                System.out.println(">>> ĐANG GỌI HÀM MERGE CART..."); // LOG 4
                cartService.mergeCart(sessionId, user);
                session.removeAttribute("CART_SESSION_ID"); 
            } else {
                System.out.println(">>> KHÔNG GỘP: Do SessionId null hoặc User null"); // LOG 5
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
    	
    	

        // --- PHẦN 2: CHUYỂN HƯỚNG THEO ROLE (CODE CŨ CỦA BẠN) ---
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