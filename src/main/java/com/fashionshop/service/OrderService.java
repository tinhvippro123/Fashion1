package com.fashionshop.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fashionshop.enums.OrderStatus;
import com.fashionshop.model.Order;
import com.fashionshop.model.User;

public interface OrderService {

	// Hàm đặt hàng chính
	Order placeOrder(User user, // Ai đặt (có thể null nếu khách vãng lai)
			String sessionId, // Session của khách vãng lai (để lấy giỏ hàng)

			// Thông tin người nhận (Từ Form Checkout gửi lên)
			String receiverName, String phone, String province, String district, String ward, String street,
			String note,

			String paymentMethod);

	// ADMIN: Lấy tất cả đơn hàng
	Page<Order> getAllOrders(Pageable pageable);
	Page<Order> searchOrders(String keyword, Pageable pageable);

	// ADMIN: Lấy chi tiết 1 đơn
	Order getOrderById(Long id);
	Order getOrderByIdAndUserId(Long orderId, Long userId);

	// ADMIN: Cập nhật trạng thái đơn hàng (Duyệt, Giao, Hủy)
	void updateOrderStatus(Long orderId, OrderStatus newStatus);

	List<Order> getOrdersByUser(Long userId);
	Page<Order> getOrdersByUser(Long userId, Pageable pageable);

	void cancelOrder(Long orderId, Long userId);

	Order findOrderForTracking(Long orderId, String phone);

	Double calculateTotalRevenue();

	long countByStatus(String statusName);
	
	List<Long> getOrderStatusCounts();
	
	List<Double> getRevenueLast7Days();
}