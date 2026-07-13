package com.fashionshop.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fashionshop.enums.OrderStatus;
import com.fashionshop.model.Order;
import com.fashionshop.model.User;

public interface OrderService {

	// HÃ m Ä‘áº·t hÃ ng chÃ­nh
	Order placeOrder(User user, // Ai Ä‘áº·t (cÃ³ thá»ƒ null náº¿u khÃ¡ch vÃ£ng lai)
			String sessionId, // Session cá»§a khÃ¡ch vÃ£ng lai (Ä‘á»ƒ láº¥y giá»\ufffd hÃ ng)

			// ThÃ´ng tin ngÆ°á»\ufffdi nháº­n (Tá»« Form Checkout gá»­i lÃªn)
			String receiverName, String phone, String province, String district, String ward, String street,
			String note,

			String paymentMethod);

	// ADMIN: Láº¥y táº¥t cáº£ Ä‘Æ¡n hÃ ng
	Page<Order> getAllOrders(Pageable pageable);
	Page<Order> searchOrders(String keyword, Pageable pageable);

	// ADMIN: Láº¥y chi tiáº¿t 1 Ä‘Æ¡n
	Order getOrderById(Long id);
	Order getOrderByIdAndUserId(Long orderId, Long userId);
	Order getOrderByIdAndEmail(Long orderId, String email);
	void cancelOrderByEmail(Long orderId, String email);

	// ADMIN: Cáº­p nháº­t tráº¡ng thÃ¡i Ä‘Æ¡n hÃ ng (Duyá»‡t, Giao, Há»§y)
	void updateOrderStatus(Long orderId, OrderStatus newStatus);

	List<Order> getOrdersByUser(Long userId);
	Page<Order> getOrdersByUser(Long userId, Pageable pageable);

	void cancelOrder(Long orderId, Long userId);

	Order findOrderForTracking(Long orderId, String phone);

	Double calculateTotalRevenue();

	long countByStatus(String statusName);
	
	List<Long> getOrderStatusCounts();
	
	List<Double> getRevenueLast7Days();

	java.util.Map<String, Object> placeOrderData(String email, String sessionId, String receiverName, String phone, String province, String district, String ward, String street, String note, String paymentMethod);
	java.util.Map<String, Object> getOrdersDataByUser(String email, int page);
}