package com.fashionshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "colors")
public class Color {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(columnDefinition = "nvarchar(50)", nullable = false)
	private String name; // Tên màu: Đỏ, Xanh...

	@Column(name = "hex_code", length = 7)
	private String hexCode; // Mã màu hiển thị: #FF0000

	public Color() {
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

	public String getHexCode() {
		return hexCode;
	}

	public void setHexCode(String hexCode) {
		this.hexCode = hexCode;
	}
}