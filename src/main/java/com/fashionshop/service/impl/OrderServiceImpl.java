package com.fashionshop.service.impl;
import com.fashionshop.enums.OrderStatus;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

import com.fashionshop.enums.OrderStatus;
import com.fashionshop.enums.PaymentMethod;
import com.fashionshop.enums.PaymentStatus;
import com.fashionshop.model.Cart;
import com.fashionshop.model.CartItem;
import com.fashionshop.model.Order;
import com.fashionshop.model.OrderItem;
import com.fashionshop.model.Payment;
import com.fashionshop.model.Product;
import com.fashionshop.model.ProductColor;
import com.fashionshop.model.User;
import com.fashionshop.model.Variant;
import com.fashionshop.repository.OrderRepository;
import com.fashionshop.repository.VariantRepository;
import com.fashionshop.service.CartService;
import com.fashionshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	private com.fashionshop.service.UserService userService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartService cartService; // Ä\ufffdá»ƒ láº¥y giá»\ufffd hÃ ng
	@Autowired
	private VariantRepository variantRepository;

	@Override
	@Transactional
	public Order placeOrder(User user, String sessionId, String receiverName, String phone, String province,
			String district, String ward, String street, String note, String paymentMethod) {

		// 1. Láº¥y giá»\ufffd hÃ ng hiá»‡n táº¡i
		Cart cart = null;
		if (user != null) {
			cart = cartService.getCartByUser(user.getId());
		} else {
			cart = cartService.getCartBySession(sessionId);
		}

		if (cart == null || cart.getItems().isEmpty()) {
			throw new FashionShopException(ErrorCode.BAD_REQUEST, "Giá»\ufffd hÃ ng trá»‘ng, khÃ´ng thá»ƒ Ä‘áº·t hÃ ng");
		}

		// 2. Táº¡o Ä‘á»‘i tÆ°á»£ng Order (Má»›i)
		Order order = new Order();
		order.setUser(user); // CÃ³ thá»ƒ null
		order.setReceiverName(receiverName);
		order.setPhone(phone);
		order.setProvince(province);
		order.setDistrict(district);
		order.setWard(ward);
		order.setStreet(street);
		order.setNote(note); // Náº¿u Entity Order cÃ³ trÆ°á»\ufffdng note
		order.setStatus(OrderStatus.PENDING); // Chá»\ufffd xá»­ lÃ½

		// 3. Chuyá»ƒn Ä‘á»•i CartItem -> OrderItem (SNAPSHOT Dá»® LIá»†U)
		List<OrderItem> orderItems = new ArrayList<>();
		double totalAmount = 0;

		for (CartItem cartItem : cart.getItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order); // Link vá»›i Order cha
			orderItem.setVariant(cartItem.getVariant()); // Váº«n giá»¯ link variant Ä‘á»ƒ trá»« tá»“n kho náº¿u cáº§n
			orderItem.setQuantity(cartItem.getQuantity());

			// --- SNAPSHOT: LÆ°u cá»©ng thÃ´ng tin táº¡i thá»\ufffdi Ä‘iá»ƒm mua ---
			// Láº¥y tá»«: Variant -> ProductColor -> Product
			Variant variant = cartItem.getVariant();
			ProductColor productColor = variant.getProductColor();
			Product product = productColor.getProduct();

			orderItem.setProductName(product.getName());
			orderItem.setColorName(productColor.getColor().getName());
			orderItem.setSizeName(variant.getSize().getName());

			// Láº¥y áº£nh Ä‘áº§u tiÃªn lÃ m áº£nh Ä‘áº¡i diá»‡n trong Ä‘Æ¡n hÃ ng
			if (!productColor.getImages().isEmpty()) {
				orderItem.setProductImage(productColor.getImages().get(0).getImageUrl());
			}

			// LÆ°u giÃ¡ táº¡i thá»\ufffdi Ä‘iá»ƒm mua
			// Æ¯u tiÃªn láº¥y giÃ¡ riÃªng cá»§a Variant (vÃ­ dá»¥ size XXL Ä‘áº¯t hÆ¡n), náº¿u khÃ´ng cÃ³ thÃ¬ láº¥y BasePrice
			double unitPrice = (variant.getPrice() != null && variant.getPrice() > 0) ? variant.getPrice() : product.getBasePrice();
			orderItem.setUnitPrice(unitPrice);

			// Cá»™ng dá»“n tá»•ng tiá»\ufffdn
			totalAmount += unitPrice * cartItem.getQuantity();

			orderItems.add(orderItem);
		}

		// GÃ¡n danh sÃ¡ch item vÃ o Order
		order.setOrderItems(orderItems);

		// 4. TÃ­nh toÃ¡n tiá»\ufffdn nong
		double shippingFee = 30000.0; // Giáº£ sá»­ phÃ­ ship cá»‘ Ä‘á»‹nh, sau nÃ y tÃ­nh logic riÃªng
		order.setShippingFee(shippingFee);
		order.setTotalAmount(totalAmount + shippingFee);

		// 5. Táº¡o Payment
		Payment payment = new Payment();
		payment.setOrder(order);

//		payment.setPaymentMethod(PaymentMethod.COD); // "COD" hoáº·c "VNPAY"

		try {
			payment.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
		} catch (Exception e) {
			payment.setPaymentMethod(PaymentMethod.COD); // Máº·c Ä‘á»‹nh náº¿u lá»—i
		}

		payment.setAmount(order.getTotalAmount());
		payment.setPaymentDate(LocalDateTime.now());
		payment.setPaymentStatus(PaymentStatus.UNPAID); // Máº·c Ä‘á»‹nh chÆ°a thanh toÃ¡n

		order.setPayment(payment);

		// 6. LÆ°u táº¥t cáº£ vÃ o DB (Cascade.ALL sáº½ lÆ°u cáº£ Items vÃ  Payment)
		Order savedOrder = orderRepository.save(order);

		// 7. XÃ³a sáº¡ch giá»\ufffd hÃ ng (QUAN TRá»ŒNG)
		if (user != null) {
			cartService.clearCart(user.getId(), null);
		} else {
			cartService.clearCart(null, sessionId);
		}

		return savedOrder;
	}

	// --- ADMIN METHODS ---

	@Override
	public Page<Order> getAllOrders(Pageable pageable) {
		return orderRepository.findAll(pageable);
	}

	@Override
	public Page<Order> searchOrders(String keyword, Pageable pageable) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return orderRepository.findAll(pageable);
		}
		return orderRepository.searchOrders(keyword.trim(), pageable);
	}

	@Override
	public Order getOrderById(Long id) {
		return orderRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.BAD_REQUEST, "Order not found"));
	}

	@Override
	public Order getOrderByIdAndUserId(Long id, Long userId) {
		Order order = getOrderById(id);
		if (!order.getUser().getId().equals(userId)) {
			throw new FashionShopException(ErrorCode.UNAUTHENTICATED);
		}
		return order;
	}

	@Override
	@Transactional
	public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
		Order order = getOrderById(orderId);
		OrderStatus oldStatus = order.getStatus();

		// 1. CHECK Há»¢P Lá»† (Logic má»›i thÃªm)
		// Náº¿u chuyá»ƒn tráº¡ng thÃ¡i khÃ´ng há»£p lÃ½ -> BÃ¡o lá»—i ngay
		if (!isValidStatusChange(oldStatus, newStatus)) {
			throw new FashionShopException(ErrorCode.BAD_REQUEST, "KhÃ´ng thá»ƒ chuyá»ƒn tá»« tráº¡ng thÃ¡i " + oldStatus + " sang " + newStatus);
		}

		// 2. LOGIC HOÃ€N TRáº¢ KHO (Ä\ufffdÃ£ lÃ m tá»« trÆ°á»›c)
		// Náº¿u tráº¡ng thÃ¡i Má»šI lÃ  Há»¦Y vÃ  tráº¡ng thÃ¡i CÅ¨ chÆ°a pháº£i Há»¦Y -> Tráº£ hÃ ng vá»\ufffd kho
		if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
			for (OrderItem item : order.getOrderItems()) {
				Variant variant = item.getVariant();
				// Cá»™ng láº¡i kho
				int currentStock = variant.getStock();
				int quantityToReturn = item.getQuantity();
				variant.setStock(currentStock + quantityToReturn);
				variantRepository.save(variant);
			}
		}

		// 3. LOGIC TRá»ª Láº I KHO (Bá»• sung cho cháº·t cháº½)
		// Náº¿u lá»¡ tay há»§y nháº§m (CANCELLED), giá»\ufffd muá»‘n khÃ´i phá»¥c láº¡i (PENDING/CONFIRMED)
		// -> Pháº£i trá»« kho láº¡i
		if (oldStatus == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
			for (OrderItem item : order.getOrderItems()) {
				Variant variant = item.getVariant();
				// Trá»« láº¡i kho
				int currentStock = variant.getStock();
				int quantityToBuy = item.getQuantity();

				if (currentStock < quantityToBuy) {
					throw new FashionShopException(ErrorCode.BAD_REQUEST, "KhÃ´ng thá»ƒ khÃ´i phá»¥c Ä‘Æ¡n hÃ ng. Sáº£n pháº©m "
							+ item.getVariant().getProductColor().getProduct().getName() + " khÃ´ng Ä‘á»§ tá»“n kho.");
				}

				variant.setStock(currentStock - quantityToBuy);
				variantRepository.save(variant);
			}
		}

//		Cáº¬P NHáº¬T TRáº NG THÃ\ufffdI
		order.setStatus(newStatus);

//		Cáº¬P NHáº¬T THANH TOÃ\ufffdN (Náº¿u giao thÃ nh cÃ´ng -> Ä\ufffdÃ¡nh dáº¥u Ä‘Ã£ tráº£ tiá»\ufffdn)
		if (newStatus == OrderStatus.COMPLETED) {
			if (order.getPayment() != null) {
				order.getPayment().setPaymentStatus(PaymentStatus.PAID);
				order.getPayment().setPaymentDate(LocalDateTime.now());
			}
		}

		orderRepository.save(order);
	}

	// --- HÃ€M PHá»¤: QUY Ä\ufffdá»ŠNH LUáº¬T CHUYá»‚N TRáº NG THÃ\ufffdI ---
	private boolean isValidStatusChange(OrderStatus oldStatus, OrderStatus newStatus) {
		// KhÃ´ng thay Ä‘á»•i gÃ¬
		if (oldStatus == newStatus)
			return true;

		// Quy táº¯c:
		// 1. Ä\ufffdÃ£ Há»¦Y (CANCELLED) thÃ¬ chá»‰ Ä‘Æ°á»£c quay láº¡i PENDING hoáº·c CONFIRMED (Ä‘á»ƒ khÃ´i
		// phá»¥c), khÃ´ng Ä‘Æ°á»£c nháº£y cÃ³c sang COMPLETED.
		if (oldStatus == OrderStatus.CANCELLED) {
			return newStatus == OrderStatus.PENDING || newStatus == OrderStatus.CONFIRMED;
		}

		// 2. Ä\ufffdÃ£ HOÃ€N THÃ€NH (COMPLETED) thÃ¬ lÃ  chá»‘t Ä‘Æ¡n, khÃ´ng Ä‘Æ°á»£c Ä‘á»•i sang tráº¡ng thÃ¡i
		// khÃ¡c (trá»« khi Return - mÃ  ta chÆ°a lÃ m logic Return).
		if (oldStatus == OrderStatus.COMPLETED) {
			return false; // Ä\ufffdÃ£ xong lÃ  xong, cáº¥m sá»­a.
		}

		// 3. Ä\ufffdÃ£ GIAO HÃ€NG (SHIPPING) thÃ¬ khÃ´ng Ä‘Æ°á»£c quay láº¡i PENDING (vÃ´ lÃ½).
		if (oldStatus == OrderStatus.SHIPPING) {
			return newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED
					|| newStatus == OrderStatus.RETURNED;
		}

		// CÃ¡c trÆ°á»\ufffdng há»£p cÃ²n láº¡i (PENDING -> CONFIRMED -> SHIPPING) Ä‘á»\ufffdu OK
		return true;
	}

	@Override
	public List<Order> getOrdersByUser(Long userId) {
		return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
	}

	@Override
	public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
		return orderRepository.findByUserIdOrderByOrderDateDesc(userId, pageable);
	}

	@Override
	@Transactional
	public void cancelOrder(Long orderId, Long userId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new FashionShopException(ErrorCode.BAD_REQUEST, "KhÃ´ng tÃ¬m tháº¥y Ä‘Æ¡n hÃ ng"));

		// 1. Kiá»ƒm tra chá»§ sá»Ÿ há»¯u (Báº£o máº­t)
		if (!order.getUser().getId().equals(userId)) {
			throw new FashionShopException(ErrorCode.UNAUTHENTICATED);
		}

		// 2. Kiá»ƒm tra tráº¡ng thÃ¡i (Chá»‰ cho há»§y khi Ä‘ang PENDING)
		// Náº¿u báº¡n muá»‘n cho há»§y cáº£ lÃºc CONFIRMED thÃ¬ thÃªm vÃ o Ä‘iá»\ufffdu kiá»‡n
		if (order.getStatus() != OrderStatus.PENDING) {
			throw new FashionShopException(ErrorCode.VALIDATION_ERROR);
		}

		// 3. Cáº­p nháº­t tráº¡ng thÃ¡i
		order.setStatus(OrderStatus.CANCELLED);
		orderRepository.save(order);

		// 4. HOÃ€N TRáº¢ Tá»’N KHO (Cá»™ng láº¡i sá»‘ lÆ°á»£ng sáº£n pháº©m)
		for (OrderItem item : order.getOrderItems()) {
			Variant variant = item.getVariant();
			int currentStock = variant.getStock();
			int quantityToReturn = item.getQuantity();

			variant.setStock(currentStock + quantityToReturn);
			variantRepository.save(variant);
		}
	}

	@Override
	public Order findOrderForTracking(Long orderId, String phone) {
		return orderRepository.findByIdAndPhone(orderId, phone)
				.orElseThrow(() -> new FashionShopException(ErrorCode.BAD_REQUEST, "KhÃ´ng tÃ¬m tháº¥y Ä‘Æ¡n hÃ ng hoáº·c sá»‘ Ä‘iá»‡n thoáº¡i khÃ´ng khá»›p!"));
	}

//	 HÃ m tÃ­nh tá»•ng doanh thu
	public Double calculateTotalRevenue() {
		return orderRepository.sumTotalRevenue();
	}

	// HÃ m Ä‘áº¿m Ä‘Æ¡n theo tráº¡ng thÃ¡i (nháº­n vÃ o String tá»« Controller vÃ  chuyá»ƒn sang
	// Enum)
	public long countByStatus(String statusName) {
		try {
			// Chuyá»ƒn chuá»—i "PENDING" thÃ nh Enum OrderStatus.PENDING
			OrderStatus status = OrderStatus.valueOf(statusName.toUpperCase());
			return orderRepository.countByStatus(status);
		} catch (IllegalArgumentException e) {
			return 0; // Tráº£ vá»\ufffd 0 náº¿u tÃªn tráº¡ng thÃ¡i khÃ´ng Ä‘Ãºng
		}
	}
	
//	dashboard
	// 1. HÃ m láº¥y dá»¯ liá»‡u cho biá»ƒu Ä‘á»“ trÃ²n (Pie Chart)
	public List<Long> getOrderStatusCounts() {
	    List<Long> counts = new ArrayList<>();
	    // Thá»© tá»± nÃ y pháº£i khá»›p vá»›i thá»© tá»± label trong biá»ƒu Ä‘á»“ JS: [HoÃ n thÃ nh, Ä\ufffdang giao, Chá»\ufffd xá»­ lÃ½]
	    counts.add(orderRepository.countByStatus(OrderStatus.COMPLETED)); // Hoáº·c COMPLETED
	    counts.add(orderRepository.countByStatus(OrderStatus.SHIPPING));
	    counts.add(orderRepository.countByStatus(OrderStatus.PENDING));
	    return counts;
	}

	// 2. HÃ m láº¥y doanh thu 7 ngÃ y gáº§n nháº¥t (Area Chart)
	public List<Double> getRevenueLast7Days() {
	    List<Double> revenueList = new ArrayList<>();
	    LocalDate today = LocalDate.now();

	    // Láº·p tá»« 6 ngÃ y trÆ°á»›c Ä‘áº¿n hÃ´m nay
	    for (int i = 6; i >= 0; i--) {
	        LocalDate date = today.minusDays(i);
	        
	        // Gá»\ufffdi Repository Ä‘á»ƒ tÃ­nh tá»•ng tiá»\ufffdn theo ngÃ y (Báº¡n cáº§n viáº¿t thÃªm hÃ m nÃ y trong Repo)
	        Double dailyRevenue = orderRepository.sumRevenueByDate(date);
	        
	        if (dailyRevenue == null) {
	            revenueList.add(0.0);
	        } else {
	            revenueList.add(dailyRevenue);
	        }
	    }
	    return revenueList;
	}
	
	

    @Override
    public java.util.Map<String, Object> placeOrderData(String email, String sessionId, String receiverName, String phone, String province, String district, String ward, String street, String note, String paymentMethod) {
        User user = null;
        if (email != null) {
            user = userService.getUserByEmailOrThrow(email);
        }
        Order order = placeOrder(user, sessionId, receiverName, phone, province, district, ward, street, note, paymentMethod);
        java.util.Map<String, Object> responseData = new java.util.HashMap<>();
        responseData.put("orderId", order.getId());
        responseData.put("message", "Ä\ufffdáº·t hÃ ng thÃ nh cÃ´ng!");
        return responseData;
    }

    @Override
    public java.util.Map<String, Object> getOrdersDataByUser(String email, int page) {
        User user = userService.getUserByEmailOrThrow(email);
        org.springframework.data.domain.Page<Order> orderPage = getOrdersByUser(user.getId(), org.springframework.data.domain.PageRequest.of(page, 10));
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("orders", orderPage.getContent());
        response.put("currentPage", orderPage.getNumber());
        response.put("totalPages", orderPage.getTotalPages());
        response.put("totalItems", orderPage.getTotalElements());
        return response;
    }

    @Override
    public Order getOrderByIdAndEmail(Long orderId, String email) {
        User user = userService.getUserByEmailOrThrow(email);
        return getOrderByIdAndUserId(orderId, user.getId());
    }

    @Override
    public void cancelOrderByEmail(Long orderId, String email) {
        User user = userService.getUserByEmailOrThrow(email);
        cancelOrder(orderId, user.getId());
    }
}
