package com.fashionshop.controller.admin;

import com.fashionshop.enums.OrderStatus;
import com.fashionshop.model.Cart;
import com.fashionshop.model.Order;
import com.fashionshop.service.CartService;
import com.fashionshop.service.OrderService;

import jakarta.servlet.http.HttpSession;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private CartService cartService;
	
	// 1. Danh sách đơn hàng
	@GetMapping
	public String listOrders(Model model) {
		model.addAttribute("orders", orderService.getAllOrders());
		return "admin/order/list"; // admin/order/list.html
	}

	// 2. Chi tiết đơn hàng
	@GetMapping("/detail/{id}")
	public String orderDetail(@PathVariable Long id, Model model) {
		Order order = orderService.getOrderById(id);
		model.addAttribute("order", order);
		// Đẩy Enum ra view để admin chọn trạng thái cập nhật
        model.addAttribute("orderStatuses", OrderStatus.values());
		return "admin/order/detail"; // admin/order/detail.html
	}

	// 3. Cập nhật trạng thái (Duyệt đơn, Hủy đơn...)
	@PostMapping("/update-status/{id}")
	public String updateStatus(@PathVariable Long id, @RequestParam("status") OrderStatus status) {
		orderService.updateOrderStatus(id, status);
		return "redirect:/admin/orders/detail/" + id;
	}
	
	@GetMapping("/checkout")
	public String checkoutPage(Model model, Principal principal, HttpSession session) {
	    
	    // 1. Lấy giỏ hàng
	    Cart cart = null;
	    if (principal != null) {
	        // Nếu đã đăng nhập -> Lấy theo User
	        cart = cartService.getCartByUser(1L); // TODO: Thay 1L bằng id lấy từ principal
	        
	        // TODO: Nếu đã đăng nhập, hãy query User từ DB và add vào model để điền sẵn tên, sđt
	        // model.addAttribute("user", userService.getUserByUsername(principal.getName()));
	    } else {
	        // Khách vãng lai
	        String sessionId = (String) session.getAttribute("CART_SESSION_ID");
	        if (sessionId != null) {
	            cart = cartService.getCartBySession(sessionId);
	        }
	    }

	    // 2. Validate: Nếu giỏ trống thì đá về trang chủ
	    if (cart == null || cart.getItems().isEmpty()) {
	        return "redirect:/";
	    }

	    // 3. Tính toán tiền
	    double totalAmount = cartService.calculateTotalPrice(cart);
	    double shippingFee = 0; // Theo ảnh của bạn là 0đ (hoặc 30k tùy logic)
	    double finalTotal = totalAmount + shippingFee;

	    model.addAttribute("cart", cart);
	    model.addAttribute("totalAmount", totalAmount);
	    model.addAttribute("shippingFee", shippingFee);
	    model.addAttribute("finalTotal", finalTotal);

	    return "client/checkout"; // Trả về checkout.html
	}
	
	
}