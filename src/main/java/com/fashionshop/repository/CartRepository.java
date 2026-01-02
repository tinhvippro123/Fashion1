package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	// Tìm giỏ hàng theo User ID (dành cho khách đã đăng nhập)
	Cart findByUserId(Long userId);

	// Tìm giỏ hàng theo Session ID (dành cho khách vãng lai)
	Cart findBySessionId(String sessionId);
}