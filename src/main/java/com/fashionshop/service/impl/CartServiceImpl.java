package com.fashionshop.service.impl;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

import com.fashionshop.model.Cart;
import com.fashionshop.model.CartItem;
import com.fashionshop.model.User;
import com.fashionshop.model.Variant;
import com.fashionshop.repository.CartRepository;
import com.fashionshop.repository.UserRepository;
import com.fashionshop.repository.VariantRepository; // Giáº£ sá»­ Ä‘Ã£ cÃ³
import com.fashionshop.service.CartService;

import java.time.LocalDateTime;

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
		// 1. TÃ¬m hoáº·c Táº¡o giá»\ufffd hÃ ng
		Cart cart = findOrCreateCart(userId, sessionId);

		// 2. Láº¥y thÃ´ng tin sáº£n pháº©m (Variant)
		Variant variant = variantRepository.findById(variantId)
				.orElseThrow(() -> new RuntimeException("Sáº£n pháº©m khÃ´ng tá»“n táº¡i"));

		// 3. Kiá»ƒm tra tá»“n kho (Stock)
		// LÆ°u Ã½: Pháº£i check tá»•ng sá»‘ lÆ°á»£ng Ä‘á»‹nh mua (trong giá»\ufffd + mua thÃªm)
		int currentQuantityInCart = 0;
		CartItem existingItem = findItemInCart(cart, variantId);

		if (existingItem != null) {
			currentQuantityInCart = existingItem.getQuantity();
		}

		if (variant.getStock() < (currentQuantityInCart + quantity)) {
			throw new FashionShopException(ErrorCode.BAD_REQUEST, "Xin lá»—i, sáº£n pháº©m nÃ y chá»‰ cÃ²n láº¡i " + variant.getStock() + " sáº£n pháº©m.");
		}

		// 4. ThÃªm vÃ o giá»\ufffd hoáº·c Cáº­p nháº­t sá»‘ lÆ°á»£ng
		if (existingItem != null) {
			// TrÆ°á»\ufffdng há»£p A: Ä\ufffdÃ£ cÃ³ -> Cá»™ng dá»“n
			existingItem.setQuantity(currentQuantityInCart + quantity);
			existingItem.setAddedAt(LocalDateTime.now());
			// náº¿u muá»‘n
		} else {
			// TrÆ°á»\ufffdng há»£p B: ChÆ°a cÃ³ -> Táº¡o má»›i
			CartItem newItem = new CartItem();
			newItem.setCart(cart);
			newItem.setVariant(variant);
			newItem.setQuantity(quantity);

			cart.getItems().add(newItem);
		}

		return cartRepository.save(cart);
	}

	@Override
	@Transactional
	public Cart updateQuantity(Long userId, String sessionId, Long cartItemId, int newQuantity) {
		Cart cart = findOrCreateCart(userId, sessionId);

		// TÃ¬m item trong giá»\ufffd
		CartItem itemToUpdate = null;
		for (CartItem item : cart.getItems()) {
			if (item.getId().equals(cartItemId)) {
				itemToUpdate = item;
				break;
			}
		}

		if (itemToUpdate == null) {
			throw new FashionShopException(ErrorCode.BAD_REQUEST, "Sáº£n pháº©m khÃ´ng cÃ³ trong giá»\ufffd hÃ ng");
		}

		// Check sá»‘ lÆ°á»£ng > 0
		if (newQuantity <= 0) {
			// Náº¿u update vá»\ufffd 0 thÃ¬ xÃ³a luÃ´n
			cart.getItems().remove(itemToUpdate);
		} else {
			// Check tá»“n kho
			Variant variant = itemToUpdate.getVariant();
			if (variant.getStock() < newQuantity) {
				throw new FashionShopException(ErrorCode.BAD_REQUEST, "Kho khÃ´ng Ä‘á»§ hÃ ng");
			}
			itemToUpdate.setQuantity(newQuantity);
		}

		return cartRepository.save(cart);
	}

//	@Override
//	@Transactional
//	public Cart removeFromCart(Long userId, String sessionId, Long cartItemId) {
//		Cart cart = findOrCreateCart(userId, sessionId);
//
//		// DÃ¹ng removeIf cá»§a Java 8 cho gá»\ufffdn
//		boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
//
//		if (!removed) {
//			throw new RuntimeException("KhÃ´ng tÃ¬m tháº¥y sáº£n pháº©m Ä‘á»ƒ xÃ³a");
//		}
//
//		return cartRepository.save(cart);
//	}

	@Override
	@Transactional
	public Cart removeFromCart(Long userId, String sessionId, Long cartItemId) {
		// 1. TÃ¬m giá»\ufffd hÃ ng (Thay vÃ¬ findOrCreate, ta chá»‰ tÃ¬m thÃ´i)
		// Náº¿u chÆ°a cÃ³ giá»\ufffd hÃ ng thÃ¬ láº¥y Ä‘Ã¢u ra mÃ  xÃ³a? -> Return null luÃ´n cho nhanh.
		Cart cart = null;
		if (userId != null) {
			cart = cartRepository.findByUserId(userId);
		} else {
			cart = cartRepository.findBySessionId(sessionId);
		}

		// Náº¿u khÃ´ng tÃ¬m tháº¥y giá»\ufffd hÃ ng -> KhÃ´ng lÃ m gÃ¬ cáº£
		if (cart == null) {
			return null;
		}

		// 2. DÃ¹ng removeIf Ä‘á»ƒ xÃ³a item khá»\ufffdi danh sÃ¡ch trong bá»™ nhá»› (Memory)
		// Khi save, Hibernate sáº½ tháº¥y list bá»‹ thiáº¿u 1 cÃ¡i -> Tá»± Ä‘á»™ng xÃ³a trong DB
		boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

		// 3. Náº¿u khÃ´ng cÃ³ gÃ¬ thay Ä‘á»•i thÃ¬ return cart cÅ©
		if (!removed) {
			return cart;
		}

		// 4. LÆ°u láº¡i Cart (Hibernate sáº½ xÃ³a orphan item vÃ  cáº­p nháº­t láº¡i list)
		return cartRepository.save(cart);
	}

	@Override
	@Transactional
	public void clearCart(Long userId, String sessionId) {
		Cart cart = findOrCreateCart(userId, sessionId);
		cart.getItems().clear(); // OrphanRemoval = true sáº½ tá»± xÃ³a trong DB
		cartRepository.save(cart);
	}

	@Override
	@Transactional
	public void mergeCart(String sessionId, User user) {
		// 1. TÃ¬m giá»\ufffd hÃ ng Session cÅ©
		Cart sessionCart = cartRepository.findBySessionId(sessionId);
		// Náº¿u giá»\ufffd Session khÃ´ng cÃ³ gÃ¬ thÃ¬ thÃ´i, thoÃ¡t luÃ´n
		if (sessionCart == null || sessionCart.getItems().isEmpty()) {
			return;
		}

		// 2. Láº¥y (hoáº·c táº¡o má»›i) giá»\ufffd hÃ ng User
		Cart userCart = cartRepository.findByUserId(user.getId());
		if (userCart == null) {
			userCart = new Cart();
			userCart.setUser(user);
			userCart = cartRepository.save(userCart);
		}

		// 3. Chuyá»ƒn item tá»« Session -> User
		for (CartItem sessionItem : sessionCart.getItems()) {
			boolean isExist = false;

			// Kiá»ƒm tra trÃ¹ng sáº£n pháº©m
			for (CartItem userItem : userCart.getItems()) {
				if (userItem.getVariant().getId().equals(sessionItem.getVariant().getId())) {
					userItem.setQuantity(userItem.getQuantity() + sessionItem.getQuantity());
					isExist = true;
					break;
				}
			}

			// Náº¿u chÆ°a cÃ³ -> ThÃªm má»›i
			if (!isExist) {
				CartItem newItem = new CartItem();
				newItem.setCart(userCart);
				newItem.setVariant(sessionItem.getVariant());
				newItem.setQuantity(sessionItem.getQuantity());
				userCart.getItems().add(newItem);
			}
		}

		// 4. LÆ°u giá»\ufffd User vÃ  XÃ³a giá»\ufffd Session
		cartRepository.save(userCart); // LÆ°u danh sÃ¡ch item má»›i
		cartRepository.delete(sessionCart); // XÃ³a giá»\ufffd cÅ©
	}

	private Cart findOrCreateCart(Long userId, String sessionId) {
		Cart cart = null;

		// Æ¯u tiÃªn tÃ¬m theo User náº¿u Ä‘Ã£ Ä‘Äƒng nháº­p
		if (userId != null) {
			cart = cartRepository.findByUserId(userId);
			if (cart == null) {
				cart = new Cart();
				User user = userRepository.findById(userId).orElse(null);
				cart.setUser(user);
			}
		}
		// Náº¿u khÃ´ng thÃ¬ tÃ¬m theo Session
		else if (sessionId != null) {
			cart = cartRepository.findBySessionId(sessionId);
			if (cart == null) {
				cart = new Cart();
				cart.setSessionId(sessionId);
			}
		}

		if (cart == null) {
			// TrÆ°á»\ufffdng há»£p hiáº¿m: Cáº£ userId vÃ  sessionId Ä‘á»\ufffdu null
			throw new FashionShopException(ErrorCode.UNAUTHENTICATED, "KhÃ´ng xÃ¡c Ä‘á»‹nh Ä‘Æ°á»£c ngÆ°á»\ufffdi dÃ¹ng");
		}

		// Náº¿u lÃ  cart má»›i (chÆ°a cÃ³ ID), cáº§n save láº§n Ä‘áº§u Ä‘á»ƒ cÃ³ ID (tÃ¹y logic, á»Ÿ Ä‘Ã¢y
		// save cuá»‘i hÃ m cÅ©ng Ä‘c)
		return cart;
	}

	private CartItem findItemInCart(Cart cart, Long variantId) {
		if (cart.getItems() == null)
			return null;

		for (CartItem item : cart.getItems()) {
			// So sÃ¡nh Variant ID
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
				// Láº¥y giÃ¡ tá»« Variant, náº¿u khÃ´ng cÃ³ má»›i láº¥y BasePrice cá»§a Product
				Double variantPrice = item.getVariant().getPrice();
				double basePrice = item.getVariant().getProductColor().getProduct().getBasePrice();
				double itemPrice = (variantPrice != null && variantPrice > 0) ? variantPrice : basePrice;
				total += itemPrice * item.getQuantity();
			}
		}
		return total;
	}

    private String getOrCreateSessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            return java.util.UUID.randomUUID().toString();
        }
        return sessionId;
    }

    private Cart resolveCartByEmail(String email, String sessionId) {
        if (email != null) {
            User user = userRepository.findByEmail(email);
            if (user != null) return getCartByUser(user.getId());
        }
        return getCartBySession(sessionId);
    }

    private Long getUserIdByEmail(String email) {
        if (email == null) return null;
        User user = userRepository.findByEmail(email);
        return (user != null) ? user.getId() : null;
    }

    @Override
    public java.util.Map<String, Object> viewCartData(String userEmail, String sessionIdHeader) {
        String sessionId = getOrCreateSessionId(sessionIdHeader);
        Cart cart = resolveCartByEmail(userEmail, sessionId);
        double totalPrice = (cart != null) ? calculateTotalPrice(cart) : 0;

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("cart", cart);
        response.put("totalPrice", totalPrice);
        response.put("sessionId", sessionId);
        return response;
    }

    @Override
    public java.util.Map<String, Object> addToCartData(String userEmail, String sessionIdHeader, Long variantId, int quantity) {
        String sessionId = getOrCreateSessionId(sessionIdHeader);
        Long userId = getUserIdByEmail(userEmail);

        addToCart(userId, sessionId, variantId, quantity);
        
        Cart updatedCart = resolveCartByEmail(userEmail, sessionId);
        int totalItems = (updatedCart != null) ? updatedCart.getTotalItems() : 0;

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("message", "ThÃªm vÃ o giá»\ufffd hÃ ng thÃ nh cÃ´ng!");
        response.put("totalItems", totalItems);
        response.put("sessionId", sessionId);

        return response;
    }

    @Override
    public void updateQuantityByEmail(String email, String sessionId, Long cartItemId, int quantity) {
        Long userId = getUserIdByEmail(email);
        updateQuantity(userId, sessionId, cartItemId, quantity);
    }

    @Override
    public void removeFromCartByEmail(String email, String sessionId, Long cartItemId) {
        Long userId = getUserIdByEmail(email);
        removeFromCart(userId, sessionId, cartItemId);
    }
}
