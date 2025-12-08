package com.fashionshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Link ngược lại Order
	@OneToOne
	@JoinColumn(name = "order_id")
	private Order order;

	@Column(name = "payment_method")
	private String paymentMethod; // COD, VNPAY...

	private Double amount;

	@Column(name = "payment_status")
	private String paymentStatus; // UNPAID, PAID...

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@Column(name = "transaction_id")
	private String transactionId;

	// --- CONSTRUCTOR ---
	public Payment() {
	}

	// --- GETTERS & SETTERS ---
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	@PrePersist
	protected void onCreate() {
		if (this.paymentDate == null)
			this.paymentDate = LocalDateTime.now();
	}
}