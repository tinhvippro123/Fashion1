package com.fashionshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fashionshop.enums.OrderStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@com.fasterxml.jackson.annotation.JsonIdentityInfo(generator = com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@com.fasterxml.jackson.annotation.JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "receiver_name", columnDefinition = "varchar(100)")
	private String receiverName;

	@Column(length = 10)
	private String phone;
	
	@Column(columnDefinition = "varchar(100)")
	private String province;
	
	@Column(columnDefinition = "varchar(100)")
	private String district;
	
	@Column(columnDefinition = "varchar(100)")
	private String ward;
	
	@Column(columnDefinition = "varchar(100)")
	private String street;

	@Column(columnDefinition = "varchar(100)")
	private String note;

	@Column(name = "order_date", updatable = false)
	private LocalDateTime orderDate;

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

	@PrePersist
	protected void onCreate() {
		this.orderDate = LocalDateTime.now();
		if (this.status == null)
			this.status = OrderStatus.PENDING; // Mặc định là PENDING
	}
}
