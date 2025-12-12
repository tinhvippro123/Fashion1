package com.fashionshop.controller.client;

import com.fashionshop.model.Cart;
import com.fashionshop.model.User;
import com.fashionshop.service.CartService;
import com.fashionshop.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    
    @Autowired
    private UserService userService; // BẮT BUỘC PHẢI CÓ để lấy ID thật

    // Hằng số cho tên Session
    private static final String CART_SESSION_KEY = "CART_SESSION_ID";

    // 1. Xem giỏ hàng
    @GetMapping
    public String viewCart(Model model, Principal principal, HttpSession session) {
        Cart cart = resolveCart(principal, session);
        
        // Tính tổng tiền
        double totalPrice = 0;
        if (cart != null) {
            totalPrice = cartService.calculateTotalPrice(cart);
        }

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);

        return "client/cart";
    }

    // 2. Thêm vào giỏ (Form Submit - Trang thường)
    @PostMapping("/add")
    public String addToCart(@RequestParam(value = "variantId", required = false) Long variantId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity, 
            Principal principal,
            HttpSession session, 
            HttpServletRequest request) {

        if (variantId == null) {
            return "redirect:" + request.getHeader("Referer") + "?error=missingSize";
        }

        Long userId = getUserId(principal);
        String sessionId = getCartSessionId(session);

        try {
            cartService.addToCart(userId, sessionId, variantId, quantity);
        } catch (RuntimeException e) {
            System.out.println("Lỗi thêm giỏ hàng: " + e.getMessage());
            return "redirect:" + request.getHeader("Referer") + "?error=stock";
        }

        return "redirect:/cart";
    }

    // 3. Cập nhật số lượng
    @PostMapping("/update")
    public String updateQuantity(@RequestParam("itemId") Long cartItemId, 
                                 @RequestParam("quantity") int quantity,
                                 Principal principal, 
                                 HttpSession session) {
        Long userId = getUserId(principal);
        String sessionId = getCartSessionId(session);

        cartService.updateQuantity(userId, sessionId, cartItemId, quantity);
        return "redirect:/cart";
    }

    // 4. Xóa sản phẩm
    @GetMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable("itemId") Long cartItemId, 
                                 Principal principal, 
                                 HttpSession session) {
        Long userId = getUserId(principal);
        String sessionId = getCartSessionId(session);

        cartService.removeFromCart(userId, sessionId, cartItemId);
        return "redirect:/cart";
    }

    // 5. API Thêm vào giỏ (AJAX - Quick Buy / Detail Page)
    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<?> addToCartApi(@RequestParam("variantId") Long variantId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity, 
            Principal principal,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserId(principal);
            String sessionId = getCartSessionId(session); // Quan trọng: Nếu chưa có session ID, nó sẽ tạo và lưu vào session

            cartService.addToCart(userId, sessionId, variantId, quantity);

            // Lấy lại giỏ hàng để cập nhật số liệu
            Cart updatedCart = resolveCart(principal, session);
            int totalItems = (updatedCart != null) ? updatedCart.getTotalItems() : 0;

            response.put("status", "success");
            response.put("message", "Thêm thành công!");
            response.put("totalItems", totalItems);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // 6. API Xóa item bằng AJAX (Nếu bạn dùng nút xóa không reload trang)
    @PostMapping("/api/remove")
    @ResponseBody
    public ResponseEntity<?> removeCartItemApi(@RequestParam("id") Long cartItemId,
            Principal principal,
            HttpSession session) {
        
        Long userId = getUserId(principal);
        String sessionId = getCartSessionId(session);
        
        cartService.removeFromCart(userId, sessionId, cartItemId);
        
        return ResponseEntity.ok("Deleted");
    }

    // 7. Fragment Mini Cart
    @GetMapping("/fragment")
    public String getCartFragment(Model model, Principal principal, HttpSession session) {
        Cart cart = resolveCart(principal, session);
        
        if (cart != null) {
            model.addAttribute("globalCart", cart);
            model.addAttribute("globalCartCount", cart.getTotalItems());
            model.addAttribute("globalTotalPrice", cartService.calculateTotalPrice(cart));
        } else {
            model.addAttribute("globalCartCount", 0);
            model.addAttribute("globalTotalPrice", 0);
        }

        return "client/fragments/mini-cart :: mini-cart-content";
    }

    // --- PRIVATE HELPER METHODS ---

    private Cart resolveCart(Principal principal, HttpSession session) {
        if (principal != null) {
            Long userId = getUserId(principal);
            return cartService.getCartByUser(userId);
        } else {
            return cartService.getCartBySession(getCartSessionId(session));
        }
    }

    private String getCartSessionId(HttpSession session) {
        String sessionId = (String) session.getAttribute(CART_SESSION_KEY);
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            session.setAttribute(CART_SESSION_KEY, sessionId);
        }
        return sessionId;
    }

    // HÀM LẤY ID THẬT TỪ DATABASE (Đã sửa lại chuẩn)
    private Long getUserId(Principal principal) {
        if (principal == null) return null;
        User user = userService.findByEmail(principal.getName());
        return (user != null) ? user.getId() : null;
    }
}