package com.fashionshop.service.impl;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartService cartService; // Để lấy giỏ hàng
	@Autowired
	private VariantRepository variantRepository;

	@Override
	@Transactional // Quan trọng: Nếu lỗi ở bước nào thì rollback toàn bộ
	public Order placeOrder(User user, String sessionId, String receiverName, String phone, String province,
			String district, String ward, String street, String note, String paymentMethod) {

		// 1. Lấy giỏ hàng hiện tại
		Cart cart = null;
		if (user != null) {
			cart = cartService.getCartByUser(user.getId());
		} else {
			cart = cartService.getCartBySession(sessionId);
		}

		if (cart == null || cart.getItems().isEmpty()) {
			throw new RuntimeException("Giỏ hàng trống, không thể đặt hàng");
		}

		// 2. Tạo đối tượng Order (Mới)
		Order order = new Order();
		order.setUser(user); // Có thể null
		order.setReceiverName(receiverName);
		order.setPhone(phone);
		order.setProvince(province);
		order.setDistrict(district);
		order.setWard(ward);
		order.setStreet(street);
		// order.setNote(note); // Nếu Entity Order có trường note
		order.setStatus(OrderStatus.PENDING); // Chờ xử lý

		// 3. Chuyển đổi CartItem -> OrderItem (SNAPSHOT DỮ LIỆU)
		List<OrderItem> orderItems = new ArrayList<>();
		double totalAmount = 0;

		for (CartItem cartItem : cart.getItems()) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order); // Link với Order cha
			orderItem.setVariant(cartItem.getVariant()); // Vẫn giữ link variant để trừ tồn kho nếu cần
			orderItem.setQuantity(cartItem.getQuantity());

			// --- SNAPSHOT: Lưu cứng thông tin tại thời điểm mua ---
			// Lấy từ: Variant -> ProductColor -> Product
			Variant variant = cartItem.getVariant();
			ProductColor productColor = variant.getProductColor();
			Product product = productColor.getProduct();

			orderItem.setProductName(product.getName());
			orderItem.setColorName(productColor.getColor().getName());
			orderItem.setSizeName(variant.getSize().getName());

			// Lấy ảnh đầu tiên làm ảnh đại diện trong đơn hàng
			if (!productColor.getImages().isEmpty()) {
				orderItem.setProductImage(productColor.getImages().get(0).getImageUrl());
			}

			// Lưu giá tại thời điểm mua
			double unitPrice = product.getBasePrice();
			orderItem.setUnitPrice(unitPrice);

			// Cộng dồn tổng tiền
			totalAmount += unitPrice * cartItem.getQuantity();

			orderItems.add(orderItem);
		}

		// Gán danh sách item vào Order
		order.setOrderItems(orderItems);

		// 4. Tính toán tiền nong
		double shippingFee = 30000.0; // Giả sử phí ship cố định, sau này tính logic riêng
		order.setShippingFee(shippingFee);
		order.setTotalAmount(totalAmount + shippingFee);

		// 5. Tạo Payment
		Payment payment = new Payment();
		payment.setOrder(order);
		payment.setPaymentMethod(PaymentMethod.COD); // "COD" hoặc "VNPAY"
		payment.setAmount(order.getTotalAmount());
		payment.setPaymentDate(LocalDateTime.now());
		payment.setPaymentStatus(PaymentStatus.UNPAID); // Mặc định chưa thanh toán

		order.setPayment(payment);

		// 6. Lưu tất cả vào DB (Cascade.ALL sẽ lưu cả Items và Payment)
		Order savedOrder = orderRepository.save(order);

		// 7. Xóa sạch giỏ hàng (QUAN TRỌNG)
		if (user != null) {
			cartService.clearCart(user.getId(), null);
		} else {
			cartService.clearCart(null, sessionId);
		}

		return savedOrder;
	}

	// --- ADMIN METHODS ---

	@Override
	public List<Order> getAllOrders() {
		// Lấy tất cả, sắp xếp mới nhất lên đầu
		return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "orderDate"));
	}

	@Override
	public Order getOrderById(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng ID: " + id));
	}

	@Override
	@Transactional
	public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
		Order order = getOrderById(orderId);
		OrderStatus oldStatus = order.getStatus();

		// 1. CHECK HỢP LỆ (Logic mới thêm)
		// Nếu chuyển trạng thái không hợp lý -> Báo lỗi ngay
		if (!isValidStatusChange(oldStatus, newStatus)) {
			throw new RuntimeException("Không thể chuyển từ trạng thái " + oldStatus + " sang " + newStatus);
		}

		// 2. LOGIC HOÀN TRẢ KHO (Đã làm từ trước)
		// Nếu trạng thái MỚI là HỦY và trạng thái CŨ chưa phải HỦY -> Trả hàng về kho
		if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
			for (OrderItem item : order.getOrderItems()) {
				Variant variant = item.getVariant();
				// Cộng lại kho
				int currentStock = variant.getStock();
				int quantityToReturn = item.getQuantity();
				variant.setStock(currentStock + quantityToReturn);
				variantRepository.save(variant);
			}
		}

		// 3. LOGIC TRỪ LẠI KHO (Bổ sung cho chặt chẽ)
		// Nếu lỡ tay hủy nhầm (CANCELLED), giờ muốn khôi phục lại (PENDING/CONFIRMED)
		// -> Phải trừ kho lại
		if (oldStatus == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
			for (OrderItem item : order.getOrderItems()) {
				Variant variant = item.getVariant();
				// Trừ lại kho
				int currentStock = variant.getStock();
				int quantityToBuy = item.getQuantity();

				if (currentStock < quantityToBuy) {
					throw new RuntimeException("Không thể khôi phục đơn hàng. Sản phẩm "
							+ variant.getProductColor().getProduct().getName() + " đã hết hàng trong kho!");
				}

				variant.setStock(currentStock - quantityToBuy);
				variantRepository.save(variant);
			}
		}

		// 4. CẬP NHẬT TRẠNG THÁI
		order.setStatus(newStatus);

		// 5. CẬP NHẬT THANH TOÁN (Nếu giao thành công -> Đánh dấu đã trả tiền)
		if (newStatus == OrderStatus.COMPLETED) {
			if (order.getPayment() != null) {
				order.getPayment().setPaymentStatus(PaymentStatus.PAID);
				order.getPayment().setPaymentDate(LocalDateTime.now());
			}
		}

		orderRepository.save(order);
	}

	// --- HÀM PHỤ: QUY ĐỊNH LUẬT CHUYỂN TRẠNG THÁI ---
	private boolean isValidStatusChange(OrderStatus oldStatus, OrderStatus newStatus) {
		// Không thay đổi gì
		if (oldStatus == newStatus)
			return true;

		// Quy tắc:
		// 1. Đã HỦY (CANCELLED) thì chỉ được quay lại PENDING hoặc CONFIRMED (để khôi
		// phục), không được nhảy cóc sang COMPLETED.
		if (oldStatus == OrderStatus.CANCELLED) {
			return newStatus == OrderStatus.PENDING || newStatus == OrderStatus.CONFIRMED;
		}

		// 2. Đã HOÀN THÀNH (COMPLETED) thì là chốt đơn, không được đổi sang trạng thái
		// khác (trừ khi Return - mà ta chưa làm logic Return).
		if (oldStatus == OrderStatus.COMPLETED) {
			return false; // Đã xong là xong, cấm sửa.
		}

		// 3. Đã GIAO HÀNG (SHIPPING) thì không được quay lại PENDING (vô lý).
		if (oldStatus == OrderStatus.SHIPPING) {
			return newStatus == OrderStatus.COMPLETED || newStatus == OrderStatus.CANCELLED
					|| newStatus == OrderStatus.RETURNED;
		}

		// Các trường hợp còn lại (PENDING -> CONFIRMED -> SHIPPING) đều OK
		return true;
	}

}