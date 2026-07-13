package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.WishlistItem;
import com.fashionshop.service.UserService;
import com.fashionshop.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<List<WishlistItem>> getUserWishlist(Principal principal) {
        List<WishlistItem> wishlist = wishlistService.getUserWishlistByEmail(principal.getName());
        return ApiResponse.success(wishlist);
    }

    @PostMapping("/toggle/{productId}")
    public ApiResponse<String> toggleWishlist(@PathVariable Long productId, Principal principal) {
        boolean isAdded = wishlistService.toggleWishlistByEmail(principal.getName(), productId);
        return ApiResponse.success(isAdded ? "Ä\ufffdÃ£ thÃªm vÃ o danh sÃ¡ch yÃªu thÃ­ch" : "Ä\ufffdÃ£ xÃ³a khá»\ufffdi danh sÃ¡ch yÃªu thÃ­ch");
    }

    @GetMapping("/check/{productId}")
    public ApiResponse<Boolean> checkWishlist(@PathVariable Long productId, Principal principal) {
        boolean exists = wishlistService.isProductInWishlistByEmail(principal.getName(), productId);
        return ApiResponse.success(exists);
    }
}
