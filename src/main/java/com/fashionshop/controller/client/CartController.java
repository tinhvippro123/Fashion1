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
    private UserService userService;

    private static final String CART_SESSION_KEY = "CART_SESSION_ID";

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

    @GetMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable("itemId") Long cartItemId, 
                                 Principal principal, 
                                 HttpSession session) {
        Long userId = getUserId(principal);
        String sessionId = getCartSessionId(session);

        cartService.removeFromCart(userId, sessionId, cartItemId);
        return "redirect:/cart";
    }

    @PostMapping("/api/add")
    @ResponseBody
    public ResponseEntity<?> addToCartApi(@RequestParam("variantId") Long variantId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity, 
            Principal principal,
            HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long userId = getUserId(principal);
            String sessionId = getCartSessionId(session);

            cartService.addToCart(userId, sessionId, variantId, quantity);

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

    private Long getUserId(Principal principal) {
        if (principal == null) return null;
        User user = userService.findByEmail(principal.getName());
        return (user != null) ? user.getId() : null;
    }
}