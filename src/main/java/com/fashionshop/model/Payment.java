package com.fashionshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fashionshop.enums.PaymentMethod;
import com.fashionshop.enums.PaymentStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", length = 20)
	private PaymentMethod paymentMethod;

	private Double amount;

	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", length = 20)
	private PaymentStatus paymentStatus;

	@Column(name = "payment_date")
	private LocalDateTime paymentDate;

	@Column(name = "transaction_id", length = 100)
	private String transactionId;

	@PrePersist
	protected void onCreate() {
		if (this.paymentDate == null)
			this.paymentDate = LocalDateTime.now();
	}
}