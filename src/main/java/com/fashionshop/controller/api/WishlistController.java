package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.User;
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
        User user = userService.getUserByEmailOrThrow(principal.getName());
        List<WishlistItem> wishlist = wishlistService.getUserWishlist(user.getId());
        return ApiResponse.success(wishlist);
    }

    @PostMapping("/toggle/{productId}")
    public ApiResponse<String> toggleWishlist(@PathVariable Long productId, Principal principal) {
        User user = userService.getUserByEmailOrThrow(principal.getName());
        boolean isAdded = wishlistService.toggleWishlist(user.getId(), productId);
        return ApiResponse.success(isAdded ? "Đã thêm vào danh sách yêu thích" : "Đã xóa khỏi danh sách yêu thích");
    }

    @GetMapping("/check/{productId}")
    public ApiResponse<Boolean> checkWishlist(@PathVariable Long productId, Principal principal) {
        User user = userService.getUserByEmailOrThrow(principal.getName());
        boolean exists = wishlistService.isProductInWishlist(user.getId(), productId);
        return ApiResponse.success(exists);
    }
}
