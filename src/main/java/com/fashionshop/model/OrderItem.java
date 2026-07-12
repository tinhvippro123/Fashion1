package com.fashionshop.model;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders_item")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Order order;

	@ManyToOne
	@JoinColumn(name = "variant_id")
	private Variant variant;

	@Column(name = "product_name", columnDefinition = "varchar(255)")
	private String productName;

	@Column(name = "color_name", columnDefinition = "varchar(50)")
	private String colorName;

	@Column(name = "size_name", columnDefinition = "varchar(10)")
	private String sizeName;

	@Column(name = "product_image", length = 500)
	private String productImage;

	private int quantity;

	@Column(name = "unit_price")
	private Double unitPrice;
}