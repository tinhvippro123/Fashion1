package com.fashionshop.service;

import com.fashionshop.model.Cart;
import com.fashionshop.model.User;

public interface CartService {

	// Lấy giỏ hàng của User (đăng nhập)
	Cart getCartByUser(Long userId);

	// Lấy giỏ hàng của Khách vãng lai (Session)
	Cart getCartBySession(String sessionId);

	// Thêm sản phẩm vào giỏ (Hàm quan trọng nhất)
	// Trả về Cart để cập nhật giao diện ngay lập tức
	Cart addToCart(Long userId, String sessionId, Long variantId, int quantity);

	// Cập nhật số lượng (khi bấm +/- trong giỏ)
	Cart updateQuantity(Long userId, String sessionId, Long cartItemId, int quantity);

	// Xóa một món khỏi giỏ
	Cart removeFromCart(Long userId, String sessionId, Long cartItemId);

	// Xóa sạch giỏ hàng
	void clearCart(Long userId, String sessionId);

	// Merge giỏ hàng (Khi khách đang chọn dở ở Session mà đăng nhập vào)
	void mergeCart(String sessionId, User user);

	double calculateTotalPrice(Cart cart);
}