package com.fashionshop.model;

import com.fashionshop.enums.ProductImageType;

import jakarta.persistence.*;

@Entity
@Table(name = "variant_images")
public class VariantImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "product_color_id")
	private ProductColor productColor;

	@Column(name = "image_url", length = 500)
	private String imageUrl;

	@Column(name = "image_type", length = 20)
	private ProductImageType imageType;

	@Column(name = "sort_order")
	private Integer sortOrder;

	public VariantImage() {
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public ProductImageType getImageType() {
		return imageType;
	}

	public void setImageType(ProductImageType imageType) {
		this.imageType = imageType;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}