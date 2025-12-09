package com.fashionshop.controller.client;

import com.fashionshop.model.Cart;
import com.fashionshop.model.Order;
import com.fashionshop.model.User;
import com.fashionshop.service.CartService;
import com.fashionshop.service.OrderService;
import com.fashionshop.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService; // Để lấy thông tin User nếu đã login

	// 1. Trang Checkout (Điền thông tin) - GET
	@GetMapping("/checkout")
	public String checkoutPage(Model model, Principal principal, HttpSession session) {
		Cart cart = resolveCart(principal, session);

		// Nếu giỏ trống -> Đá về trang chủ
		if (cart == null || cart.getItems().isEmpty()) {
			return "redirect:/";
		}

		// Tính toán tiền nong để hiển thị
		double totalAmount = cartService.calculateTotalPrice(cart);
		// Phí ship (Có thể logic phức tạp hơn, tạm thời để 0 hoặc cố định)
		double shippingFee = 0;
		double finalTotal = totalAmount + shippingFee;

		model.addAttribute("cart", cart);
		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("shippingFee", shippingFee);
		model.addAttribute("finalTotal", finalTotal);
		// Gửi biến isLoggedIn (true/false) sang HTML thay vì dùng #request
        model.addAttribute("isLoggedIn", principal != null);
		return "client/checkout";
	}

	// 2. Xử lý Đặt hàng (Hứng Form Submit) - POST
	@PostMapping("/place-order")
	public String placeOrder(
			// Các tham số này PHẢI khớp với name="" trong file checkout.html
			@RequestParam("receiverName") String receiverName, @RequestParam("phone") String phone,
			@RequestParam("province") String province, @RequestParam("district") String district,
			@RequestParam("ward") String ward, @RequestParam("street") String street,
			@RequestParam(value = "note", required = false) String note,

			// Mặc định COD nếu form không gửi lên
			@RequestParam(value = "paymentMethod", defaultValue = "COD") String paymentMethod,

			Principal principal, HttpSession session, RedirectAttributes redirectAttributes) {
		// Xác định người dùng
		User user = null;
		if (principal != null) {
			// TODO: Viết hàm findByUsername trong UserService nếu chưa có
			String email = principal.getName();
			// Tìm User theo Email
			user = userService.findByEmail(email);
		}

		String sessionId = (String) session.getAttribute("CART_SESSION_ID");

		try {
			// GỌI SERVICE ĐỂ CHỐT ĐƠN
			Order order = orderService.placeOrder(user, sessionId, receiverName, phone, province, district, ward,
					street, note, paymentMethod);

			// Đặt hàng thành công -> Chuyển sang trang thông báo thành công
			// Truyền ID đơn hàng sang để hiển thị "Cảm ơn bạn đã đặt đơn #..."
			redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công!");
			return "redirect:/order/success/" + order.getId();

		} catch (Exception e) {
			e.printStackTrace();
			// Nếu lỗi (ví dụ hết hàng) -> Quay lại trang checkout và báo lỗi
			redirectAttributes.addFlashAttribute("errorMessage", "Đặt hàng thất bại: " + e.getMessage());
			return "redirect:/order/checkout";
		}
	}

	// 3. Trang Thông báo Thành công (Thank You Page)
	@GetMapping("/success/{orderId}")
	public String successPage(@PathVariable Long orderId, Model model) {
		model.addAttribute("orderId", orderId);
		return "client/order-success"; // Bạn cần tạo file html này
	}

	// --- Private Helper ---
	private Cart resolveCart(Principal principal, HttpSession session) {
        // 1. Nếu Khách đã đăng nhập
        if (principal != null) {
            String email = principal.getName(); // Lấy email từ Spring Security
            User user = userService.findByEmail(email); // Tìm user trong DB
            
            // Nếu tìm thấy user -> Lấy giỏ hàng theo ID của user đó
            if (user != null) {
                return cartService.getCartByUser(user.getId());
            }
        } 
        
        // 2. Nếu là Khách vãng lai (hoặc lỗi không tìm thấy user)
        String sessionId = (String) session.getAttribute("CART_SESSION_ID");
        return (sessionId != null) ? cartService.getCartBySession(sessionId) : null;
    }
}