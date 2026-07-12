package com.fashionshop.model;

import jakarta.persistence.*;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_colors")
@com.fasterxml.jackson.annotation.JsonIdentityInfo(generator = com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
	private Boolean isDefault;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL)
	private List<Variant> variants;

	@OneToMany(mappedBy = "productColor", cascade = CascadeType.ALL)
	private List<VariantImage> images;
}
