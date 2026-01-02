package com.fashionshop.model;

import com.fashionshop.enums.VariantStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "variants")
public class Variant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_color_id")
	private ProductColor productColor;

	@ManyToOne
	@JoinColumn(name = "size_id")
	private Size size;

	private Double price;
	private Integer stock;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private VariantStatus status;

	public Variant() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductColor getProductColor() {
		return productColor;
	}

	public void setProductColor(ProductColor productColor) {
		this.productColor = productColor;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public VariantStatus getStatus() {
		return status;
	}

	public void setStatus(VariantStatus status) {
		this.status = status;
	}
}