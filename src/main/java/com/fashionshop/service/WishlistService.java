package com.fashionshop.service;

import com.fashionshop.model.WishlistItem;
import java.util.List;

public interface WishlistService {
    List<WishlistItem> getUserWishlist(Long userId);
    boolean toggleWishlist(Long userId, Long productId);
    boolean isProductInWishlist(Long userId, Long productId);
}
