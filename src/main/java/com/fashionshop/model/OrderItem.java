package com.fashionshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders_item")
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;

	// Vẫn link variant_id để track tồn kho nếu cần
	@ManyToOne
	@JoinColumn(name = "variant_id")
	private Variant variant;

	// --- CÁC TRƯỜNG SNAPSHOT (Lưu cứng thông tin) ---
	@Column(name = "product_name", columnDefinition = "nvarchar(255)")
	private String productName;

	@Column(name = "color_name", columnDefinition = "nvarchar(50)")
	private String colorName;

	@Column(name = "size_name", columnDefinition = "nvarchar(10)")
	private String sizeName;

	@Column(name = "product_image", length = 500)
	private String productImage;

	private int quantity;

	@Column(name = "unit_price")
	private Double unitPrice;

	// --- CONSTRUCTOR ---
	public OrderItem() {
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

	public Variant getVariant() {
		return variant;
	}

	public void setVariant(Variant variant) {
		this.variant = variant;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
}