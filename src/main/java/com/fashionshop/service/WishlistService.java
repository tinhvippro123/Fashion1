package com.fashionshop.service;

import com.fashionshop.model.WishlistItem;
import java.util.List;

public interface WishlistService {
    List<WishlistItem> getUserWishlist(Long userId);
    List<WishlistItem> getUserWishlistByEmail(String email);
    boolean toggleWishlistByEmail(String email, Long productId);
    boolean isProductInWishlistByEmail(String email, Long productId);
    boolean toggleWishlist(Long userId, Long productId);
    boolean isProductInWishlist(Long userId, Long productId);
}
