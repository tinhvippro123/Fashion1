package com.fashionshop.model;

import com.fashionshop.enums.ProductImageType;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}