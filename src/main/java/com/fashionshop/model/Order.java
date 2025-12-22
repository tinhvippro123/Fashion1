package com.fashionshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fashionshop.enums.OrderStatus;

@Entity
@Table(name = "orders")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "receiver_name", columnDefinition = "nvarchar(100)")
	private String receiverName;

	@Column(length = 10)
	private String phone;
	
	@Column(columnDefinition = "nvarchar(100)")
	private String province;
	
	@Column(columnDefinition = "nvarchar(100)")
	private String district;
	
	@Column(columnDefinition = "nvarchar(100)")
	private String ward;
	
	@Column(columnDefinition = "nvarchar(100)")
	private String street;

	@Column(columnDefinition = "nvarchar(100)")
	private String note;

	@Column(name = "order_date", updatable = false)
	private LocalDateTime orderDate;

	// Status nên dùng String hoặc Integer (0: Pending, 1: Shipping...)
	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private OrderStatus status;

	@Column(name = "shipping_fee")
	private Double shippingFee;

	@Column(name = "total_amount")
	private Double totalAmount;

	// Quan hệ 1-N với OrderItem
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	// Quan hệ 1-1 với Payment (Một đơn có 1 giao dịch thanh toán)
	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
	private Payment payment;

	// --- CONSTRUCTOR ---
	public Order() {
	}

	// --- GETTERS & SETTERS (Manual) ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Double getShippingFee() {
		return shippingFee;
	}

	public void setShippingFee(Double shippingFee) {
		this.shippingFee = shippingFee;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public Payment getPayment() {
		return payment;
	}

	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@PrePersist
	protected void onCreate() {
		this.orderDate = LocalDateTime.now();
		if (this.status == null)
			this.status = OrderStatus.PENDING; // Mặc định
	}
}