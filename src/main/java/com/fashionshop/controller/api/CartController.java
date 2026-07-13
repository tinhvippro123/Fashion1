package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.model.Cart;
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
        String email = principal != null ? principal.getName() : null;
        return ApiResponse.success(cartService.viewCartData(email, sessionIdHeader));
    }

    @PostMapping("/add")
    public ApiResponse<Map<String, Object>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionIdHeader,
            Principal principal) {
        String email = principal != null ? principal.getName() : null;
        return ApiResponse.success(cartService.addToCartData(email, sessionIdHeader, request.getVariantId(), request.getQuantity()));
    }

    @PutMapping("/update")
    public ApiResponse<String> updateQuantity(
            @Valid @RequestBody UpdateCartRequest request,
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionId,
            Principal principal) {
            
        String email = principal != null ? principal.getName() : null;
        cartService.updateQuantityByEmail(email, sessionId, request.getItemId(), request.getQuantity());
        return ApiResponse.success("Cáº­p nháº­t sá»‘ lÆ°á»£ng thÃ nh cÃ´ng");
    }

    @DeleteMapping("/remove/{itemId}")
    public ApiResponse<String> removeFromCart(
            @PathVariable("itemId") Long cartItemId,
            @RequestHeader(value = "X-Cart-Session-Id", required = false) String sessionId,
            Principal principal) {
            
        String email = principal != null ? principal.getName() : null;
        cartService.removeFromCartByEmail(email, sessionId, cartItemId);
        return ApiResponse.success("XÃ³a sáº£n pháº©m khá»\ufffdi giá»\ufffd hÃ ng thÃ nh cÃ´ng");
    }

}
