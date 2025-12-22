package com.fashionshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sizes")
public class Size {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 10)
	private String name; // S, M, L, XL, 29, 30...

	@Column(name = "sort_order")
	private Integer sortOrder; // Để sắp xếp: S đứng trước M

	public Size() {
	}

	// Getters & Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}
}