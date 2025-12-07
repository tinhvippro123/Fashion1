package com.fashionshop.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_colors")
public class ProductColor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "color_id")
	private Color color;

	@Column(name = "is_default")
	private Boolean isDefault; // Màu này có phải màu hiển thị chính ngoài trang chủ không?

	// Quan hệ 1-N với Variants (Một màu có nhiều Size: S, M, L)
	@OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL)
	private List<Variant> variants;

	// Quan hệ 1-N với Ảnh (Một màu có nhiều ảnh góc độ khác nhau)
	@OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL)
	private List<VariantImage> images;

	public ProductColor() {
	}

	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public List<Variant> getVariants() {
		return variants;
	}

	public void setVariants(List<Variant> variants) {
		this.variants = variants;
	}

	public List<VariantImage> getImages() {
		return images;
	}

	public void setImages(List<VariantImage> images) {
		this.images = images;
	}
}