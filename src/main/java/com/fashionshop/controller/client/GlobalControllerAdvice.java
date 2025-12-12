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

@ControllerAdvice(basePackages = "com.fashionshop.controller.client")
public class GlobalControllerAdvice {

    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    // --- 1. LẤY MENU RIÊNG BIỆT CHO NAM VÀ NỮ ---
    @ModelAttribute
    public void populateMenuCategories(Model model) {
        // Tìm danh mục gốc NAM (slug phải đúng là 'nam' trong database)
        Category menCat = categoryService.findBySlug("nam");
        if (menCat != null) {
            // Gửi danh sách con (Áo, Quần, Phụ kiện...) của Nam ra view
            model.addAttribute("menCategories", menCat.getChildren());
        }

        // Tìm danh mục gốc NỮ (slug phải đúng là 'nu' trong database)
        Category womenCat = categoryService.findBySlug("nu"); 
        if (womenCat != null) {
            // Gửi danh sách con (Áo, Quần, Đầm...) của Nữ ra view
            model.addAttribute("womenCategories", womenCat.getChildren());
        }
    }
    
    // --- 2. GIỎ HÀNG (GIỮ NGUYÊN CŨ) ---
    @ModelAttribute
    public void addGlobalAttributes(Model model, Principal principal, HttpSession session) {
        Cart cart = null;
        if (principal != null) {
            User user = userService.findByEmail(principal.getName());
            if (user != null) cart = cartService.getCartByUser(user.getId());
        } else {
            String sessionId = (String) session.getAttribute("CART_SESSION_ID");
            if (sessionId != null) cart = cartService.getCartBySession(sessionId);
        }

        if (cart != null) {
            model.addAttribute("globalCart", cart);
            model.addAttribute("globalCartCount", cart.getTotalItems());
            model.addAttribute("globalTotalPrice", cartService.calculateTotalPrice(cart));
        } else {
            model.addAttribute("globalCartCount", 0);
            model.addAttribute("globalTotalPrice", 0);
        }
        model.addAttribute("isLoggedIn", principal != null);
    }
}