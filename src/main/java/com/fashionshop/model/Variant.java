package com.fashionshop.model;

import com.fashionshop.enums.VariantStatus;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "variants")
@com.fasterxml.jackson.annotation.JsonIdentityInfo(generator = com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
}
