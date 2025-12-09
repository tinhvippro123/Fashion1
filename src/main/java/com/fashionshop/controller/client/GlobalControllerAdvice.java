package com.fashionshop.controller.client;

import com.fashionshop.model.Cart;
import com.fashionshop.model.Category;
import com.fashionshop.model.User;
import com.fashionshop.service.CartService;
import com.fashionshop.service.CategoryService;
import com.fashionshop.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;

@ControllerAdvice(basePackages = "com.fashionshop.controller.client") // Chỉ áp dụng cho các controller của Client
public class GlobalControllerAdvice {

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @ModelAttribute("categories") // Tên biến dùng trong HTML sẽ là ${categories}
    public List<Category> populateCategories() {
        // Logic: Chỉ lấy các danh mục GỐC (Parent = null)
        // Vì trong Entity Category mình đã map @OneToMany children, 
        // nên từ cha sẽ tự lấy được con.
        return categoryService.getAllRootCategories(); 
    }
    
    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal, HttpSession session) {
        // Logic lấy giỏ hàng (Giống hệt resolveCart bạn đã làm)
        Cart cart = null;
        
        if (principal != null) {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            if (user != null) {
                cart = cartService.getCartByUser(user.getId());
            }
        } else {
            String sessionId = (String) session.getAttribute("CART_SESSION_ID");
            if (sessionId != null) {
                cart = cartService.getCartBySession(sessionId);
            }
        }

        // Đẩy dữ liệu ra toàn bộ View
        if (cart != null) {
            model.addAttribute("globalCart", cart);
            model.addAttribute("globalCartCount", cart.getTotalItems());
            model.addAttribute("globalTotalPrice", cartService.calculateTotalPrice(cart)); // Nhớ dùng service tính tiền
        } else {
            model.addAttribute("globalCartCount", 0);
            model.addAttribute("globalTotalPrice", 0);
        }
        model.addAttribute("isLoggedIn", principal != null);
    }
}