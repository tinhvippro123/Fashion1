package com.fashionshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "varchar(100)", nullable = false)
	private String name;

	@Column(length = 200)
	private String slug;

	@Column(name = "is_active")
	private Boolean isActive;

	// Quan hệ đệ quy: Danh mục cha
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	// Quan hệ ngược: Các danh mục con (để hiển thị menu)
	@OneToMany(mappedBy = "parent")
	private List<Category> children;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
}