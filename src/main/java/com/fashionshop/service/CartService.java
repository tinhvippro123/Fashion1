package com.fashionshop.service;

import com.fashionshop.model.Cart;
import com.fashionshop.model.User;

public interface CartService {

	// Láº¥y giá»\ufffd hÃ ng cá»§a User (Ä‘Äƒng nháº­p)
	Cart getCartByUser(Long userId);

	// Láº¥y giá»\ufffd hÃ ng cá»§a KhÃ¡ch vÃ£ng lai (Session)
	Cart getCartBySession(String sessionId);

	// ThÃªm sáº£n pháº©m vÃ o giá»\ufffd (HÃ m quan trá»\ufffdng nháº¥t)
	// Tráº£ vá»\ufffd Cart Ä‘á»ƒ cáº­p nháº­t giao diá»‡n ngay láº­p tá»©c
	Cart addToCart(Long userId, String sessionId, Long variantId, int quantity);

	// Cáº­p nháº­t sá»‘ lÆ°á»£ng (khi báº¥m +/- trong giá»\ufffd)
	Cart updateQuantity(Long userId, String sessionId, Long cartItemId, int quantity);

	// XÃ³a má»™t mÃ³n khá»\ufffdi giá»\ufffd
	Cart removeFromCart(Long userId, String sessionId, Long cartItemId);
	void updateQuantityByEmail(String email, String sessionId, Long cartItemId, int quantity);
	void removeFromCartByEmail(String email, String sessionId, Long cartItemId);

	// XÃ³a sáº¡ch giá»\ufffd hÃ ng
	void clearCart(Long userId, String sessionId);

	// Merge giá»\ufffd hÃ ng (Khi khÃ¡ch Ä‘ang chá»\ufffdn dá»Ÿ á»Ÿ Session mÃ  Ä‘Äƒng nháº­p vÃ o)
	void mergeCart(String sessionId, User user);

	double calculateTotalPrice(Cart cart);

	java.util.Map<String, Object> viewCartData(String userEmail, String sessionIdHeader);
	java.util.Map<String, Object> addToCartData(String userEmail, String sessionIdHeader, Long variantId, int quantity);
}