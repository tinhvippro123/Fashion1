package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.model.Cart;
import com.fashionshop.model.User;
import com.fashionshop.service.CartService;
import com.fashionshop.service.UserService;
import com.fashionshop.dto.AddToCartRequest;
import com.fashionshop.dto.UpdateCartRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("apiCartController")
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<Map<String, Object>> viewCart(
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionIdHeader,
            Principal principal) {
            
        String sessionId = getOrCreateSessionId(sessionIdHeader);
        Cart cart = resolveCart(principal, sessionId);
        double totalPrice = (cart != null) ? cartService.calculateTotalPrice(cart) : 0;

        Map<String, Object> response = new HashMap<>();
        response.put("cart", cart);
        response.put("totalPrice", totalPrice);
        response.put("sessionId", sessionId); // Trả về để frontend lưu lại (ví dụ vào localStorage) nếu là guest

        return ApiResponse.success(response);
    }

    @PostMapping("/add")
    public ApiResponse<Map<String, Object>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionIdHeader,
            Principal principal) {

        String sessionId = getOrCreateSessionId(sessionIdHeader);
        Long userId = getUserId(principal);

        cartService.addToCart(userId, sessionId, request.getVariantId(), request.getQuantity());
        
        Cart updatedCart = resolveCart(principal, sessionId);
        int totalItems = (updatedCart != null) ? updatedCart.getTotalItems() : 0;

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Thêm vào giỏ hàng thành công!");
        response.put("totalItems", totalItems);
        response.put("sessionId", sessionId);

        return ApiResponse.success(response);
    }

    @PutMapping("/update")
    public ApiResponse<String> updateQuantity(
            @Valid @RequestBody UpdateCartRequest request,
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionId,
            Principal principal) {
            
        Long userId = getUserId(principal);
        cartService.updateQuantity(userId, sessionId, request.getItemId(), request.getQuantity());
        return ApiResponse.success("Cập nhật số lượng thành công");
    }

    @DeleteMapping("/remove/{itemId}")
    public ApiResponse<String> removeFromCart(
            @PathVariable("itemId") Long cartItemId,
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionId,
            Principal principal) {
            
        Long userId = getUserId(principal);
        cartService.removeFromCart(userId, sessionId, cartItemId);
        return ApiResponse.success("Xóa sản phẩm khỏi giỏ hàng thành công");
    }

    private String getOrCreateSessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return UUID.randomUUID().toString();
        }
        return sessionId;
    }

    private Cart resolveCart(Principal principal, String sessionId) {
        if (principal != null) {
            Long userId = getUserId(principal);
            return cartService.getCartByUser(userId);
        } else {
            return cartService.getCartBySession(sessionId);
        }
    }

    private Long getUserId(Principal principal) {
        if (principal == null) return null;
        User user = userService.getUserByEmailOrThrow(principal.getName());
        return (user != null) ? user.getId() : null;
    }
}
