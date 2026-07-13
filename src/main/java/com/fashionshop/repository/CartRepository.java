package com.fashionshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

	// TÃ¬m giá»\ufffd hÃ ng theo User ID (dÃ nh cho khÃ¡ch Ä‘Ã£ Ä‘Äƒng nháº­p)
	Cart findByUserId(Long userId);

	// TÃ¬m giá»\ufffd hÃ ng theo Session ID (dÃ nh cho khÃ¡ch vÃ£ng lai)
	Cart findBySessionId(String sessionId);
}