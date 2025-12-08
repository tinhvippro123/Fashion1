package com.fashionshop.controller.client;

import com.fashionshop.model.Cart;
import com.fashionshop.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequestMapping("/cart")
public class CartController {

	@Autowired
	private CartService cartService;

	// Hằng số cho tên Session
	private static final String CART_SESSION_KEY = "CART_SESSION_ID";

	// 1. Xem giỏ hàng
	@GetMapping
	public String viewCart(Model model, Principal principal, HttpSession session) {
		Cart cart = resolveCart(principal, session);
		model.addAttribute("cart", cart);

		// Tính tổng tiền (để hiển thị)
		double totalPrice = cartService.calculateTotalPrice(cart);
		model.addAttribute("totalPrice", totalPrice);

		return "client/cart"; // Trả về file cart.html (sẽ tạo sau)
	}

	// 2. Thêm vào giỏ (Xử lý Form từ trang chi tiết)
	@PostMapping("/add")
	public String addToCart(@RequestParam(value = "variantId", required = false) Long variantId,
			@RequestParam(value = "quantity", defaultValue = "1") int quantity, Principal principal,
			HttpSession session, HttpServletRequest request // Để quay lại trang cũ
	) {

		// Kiểm tra thủ công
		if (variantId == null) {
			// Nếu thiếu size -> Quay lại trang cũ kèm thông báo lỗi
			return "redirect:" + request.getHeader("Referer") + "?error=missingSize";
		}

		// Lấy thông tin User hoặc Session
		Long userId = (principal != null) ? getUserIdFromPrincipal(principal) : null;
		String sessionId = getCartSessionId(session);

		// Gọi Service xử lý logic
		try {
			cartService.addToCart(userId, sessionId, variantId, quantity);
		} catch (RuntimeException e) {
			// Nếu lỗi (ví dụ hết hàng), có thể lưu thông báo lỗi vào FlashAttribute (tạm
			// thời bỏ qua)
			System.out.println("Lỗi thêm giỏ hàng: " + e.getMessage());
			return "redirect:" + request.getHeader("Referer") + "?error=stock";
		}

		// Quay lại trang giỏ hàng hoặc trang hiện tại
		return "redirect:/cart";
	}

	// 3. Cập nhật số lượng (AJAX hoặc Form submit)
	@PostMapping("/update")
	public String updateQuantity(@RequestParam("itemId") Long cartItemId, @RequestParam("quantity") int quantity,
			Principal principal, HttpSession session) {
		Long userId = (principal != null) ? getUserIdFromPrincipal(principal) : null;
		String sessionId = getCartSessionId(session);

		cartService.updateQuantity(userId, sessionId, cartItemId, quantity);
		return "redirect:/cart";
	}

	// 4. Xóa sản phẩm
	@GetMapping("/remove/{itemId}")
	public String removeFromCart(@PathVariable("itemId") Long cartItemId, Principal principal, HttpSession session) {
		Long userId = (principal != null) ? getUserIdFromPrincipal(principal) : null;
		String sessionId = getCartSessionId(session);

		cartService.removeFromCart(userId, sessionId, cartItemId);
		return "redirect:/cart";
	}

	// --- PRIVATE HELPER (Xử lý Session/User) ---

	// Hàm xác định giỏ hàng hiện tại là của ai
	private Cart resolveCart(Principal principal, HttpSession session) {
		if (principal != null) {
			// Nếu đã đăng nhập -> Lấy theo User ID
			return cartService.getCartByUser(getUserIdFromPrincipal(principal));
		} else {
			// Nếu chưa đăng nhập -> Lấy theo Session ID
			return cartService.getCartBySession(getCartSessionId(session));
		}
	}

	// Hàm lấy/tạo Session ID cho khách vãng lai
	private String getCartSessionId(HttpSession session) {
		String sessionId = (String) session.getAttribute(CART_SESSION_KEY);
		if (sessionId == null) {
			sessionId = UUID.randomUUID().toString();
			session.setAttribute(CART_SESSION_KEY, sessionId);
		}
		return sessionId;
	}

	// Hàm giả lập lấy UserID từ Principal (Spring Security)
	// Bạn cần tùy chỉnh lại tùy theo cách bạn config UserDetails
	private Long getUserIdFromPrincipal(Principal principal) {
		// Ví dụ: Username là email hoặc tên, cần query DB để lấy ID
		// Ở đây mình giả định bạn sẽ có Service để lấy ID từ username
		// return userService.findByUsername(principal.getName()).getId();
		return 1L; // Tạm thời hardcode để test logic, bạn thay bằng logic thật nhé
	}
}