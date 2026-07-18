package com.fashionshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@com.fasterxml.jackson.annotation.JsonIdentityInfo(generator = com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "varchar(255)", nullable = false)
	private String name;

	@Column(length = 255)
	private String slug;

	@Column(columnDefinition = "text")
	private String description;

	@Column(length = 100)
	private String sku;

	@Column(length = 100)
	private String brand;

	@Column(length = 255)
	private String thumbnail;

	@Column(name = "original_price")
	private Double originalPrice;

	@Column(name = "discount_percent")
	private Integer discountPercent;

	@Column(name = "base_price")
	private Double basePrice;

	@Column(name = "is_active")
	private Boolean isActive;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ProductColor> productColors;
	
	@Transient
	public boolean getIsNew() {
		if (createdAt == null) return false;
		return ChronoUnit.DAYS.between(createdAt, LocalDateTime.now()) <= 7;
	}
}
