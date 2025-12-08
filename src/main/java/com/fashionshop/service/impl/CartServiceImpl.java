package com.fashionshop.service.impl;

import com.fashionshop.model.Cart;
import com.fashionshop.model.CartItem;
import com.fashionshop.model.User;
import com.fashionshop.model.Variant;
import com.fashionshop.repository.CartRepository;
import com.fashionshop.repository.UserRepository;
import com.fashionshop.repository.VariantRepository; // Giả sử đã có
import com.fashionshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private VariantRepository variantRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public Cart getCartByUser(Long userId) {
		return cartRepository.findByUserId(userId);
	}

	@Override
	public Cart getCartBySession(String sessionId) {
		return cartRepository.findBySessionId(sessionId);
	}

	@Override
	@Transactional
	public Cart addToCart(Long userId, String sessionId, Long variantId, int quantity) {
		// 1. Tìm hoặc Tạo giỏ hàng
		Cart cart = findOrCreateCart(userId, sessionId);

		// 2. Lấy thông tin sản phẩm (Variant)
		Variant variant = variantRepository.findById(variantId)
				.orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

		// 3. Kiểm tra tồn kho (Stock)
		// Lưu ý: Phải check tổng số lượng định mua (trong giỏ + mua thêm)
		int currentQuantityInCart = 0;
		CartItem existingItem = findItemInCart(cart, variantId);

		if (existingItem != null) {
			currentQuantityInCart = existingItem.getQuantity();
		}

		if (variant.getStock() < (currentQuantityInCart + quantity)) {
			throw new RuntimeException("Xin lỗi, sản phẩm này chỉ còn lại " + variant.getStock() + " sản phẩm.");
		}

		// 4. Thêm vào giỏ hoặc Cập nhật số lượng
		if (existingItem != null) {
			// Trường hợp A: Đã có -> Cộng dồn
			existingItem.setQuantity(currentQuantityInCart + quantity);
			// existingItem.setAddedAt(LocalDateTime.now()); // Có thể update lại thời gian
			// nếu muốn
		} else {
			// Trường hợp B: Chưa có -> Tạo mới
			CartItem newItem = new CartItem();
			newItem.setCart(cart);
			newItem.setVariant(variant);
			newItem.setQuantity(quantity);
			// newItem.setAddedAt(LocalDateTime.now()); // Entity đã có @PrePersist xử lý

			cart.getItems().add(newItem);
		}

		// 5. Lưu và Trả về
		return cartRepository.save(cart);
	}

	@Override
	@Transactional
	public Cart updateQuantity(Long userId, String sessionId, Long cartItemId, int newQuantity) {
		Cart cart = findOrCreateCart(userId, sessionId);

		// Tìm item trong giỏ
		CartItem itemToUpdate = null;
		for (CartItem item : cart.getItems()) {
			if (item.getId().equals(cartItemId)) {
				itemToUpdate = item;
				break;
			}
		}

		if (itemToUpdate == null) {
			throw new RuntimeException("Sản phẩm không có trong giỏ hàng");
		}

		// Check số lượng > 0
		if (newQuantity <= 0) {
			// Nếu update về 0 thì xóa luôn
			cart.getItems().remove(itemToUpdate);
		} else {
			// Check tồn kho
			Variant variant = itemToUpdate.getVariant();
			if (variant.getStock() < newQuantity) {
				throw new RuntimeException("Kho không đủ hàng");
			}
			itemToUpdate.setQuantity(newQuantity);
		}

		return cartRepository.save(cart);
	}

	@Override
	@Transactional
	public Cart removeFromCart(Long userId, String sessionId, Long cartItemId) {
		Cart cart = findOrCreateCart(userId, sessionId);

		// Dùng removeIf của Java 8 cho gọn
		boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

		if (!removed) {
			throw new RuntimeException("Không tìm thấy sản phẩm để xóa");
		}

		return cartRepository.save(cart);
	}

	@Override
	@Transactional
	public void clearCart(Long userId, String sessionId) {
		Cart cart = findOrCreateCart(userId, sessionId);
		cart.getItems().clear(); // OrphanRemoval = true sẽ tự xóa trong DB
		cartRepository.save(cart);
	}

	@Override
	@Transactional
	public void mergeCart(String sessionId, Long userId) {
		// Logic nâng cao: Khi khách đăng nhập, gộp giỏ hàng Session vào giỏ hàng User
		// (Bạn có thể để phần này làm sau cũng được)
	}

	// --- PRIVATE HELPER METHODS ---

	private Cart findOrCreateCart(Long userId, String sessionId) {
		Cart cart = null;

		// Ưu tiên tìm theo User nếu đã đăng nhập
		if (userId != null) {
			cart = cartRepository.findByUserId(userId);
			if (cart == null) {
				cart = new Cart();
				User user = userRepository.findById(userId).orElse(null);
				cart.setUser(user);
				// cart.setCreatedAt(...) // Entity tự lo
			}
		}
		// Nếu không thì tìm theo Session
		else if (sessionId != null) {
			cart = cartRepository.findBySessionId(sessionId);
			if (cart == null) {
				cart = new Cart();
				cart.setSessionId(sessionId);
			}
		}

		if (cart == null) {
			// Trường hợp hiếm: Cả userId và sessionId đều null
			throw new RuntimeException("Không xác định được người dùng");
		}

		// Nếu là cart mới (chưa có ID), cần save lần đầu để có ID (tùy logic, ở đây
		// save cuối hàm cũng đc)
		return cart;
	}

	private CartItem findItemInCart(Cart cart, Long variantId) {
		if (cart.getItems() == null)
			return null;

		for (CartItem item : cart.getItems()) {
			// So sánh Variant ID
			if (item.getVariant().getId().equals(variantId)) {
				return item;
			}
		}
		return null;
	}

	@Override
	public double calculateTotalPrice(Cart cart) {
		double total = 0;
		if (cart != null && cart.getItems() != null) {
			for (CartItem item : cart.getItems()) {
				// Lấy giá từ Variant -> Product -> BasePrice
				// Công thức: Giá * Số lượng
				double itemPrice = item.getVariant().getProductColor().getProduct().getBasePrice();
				total += itemPrice * item.getQuantity();
			}
		}
		return total;
	}
}