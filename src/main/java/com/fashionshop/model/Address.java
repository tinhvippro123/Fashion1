package com.fashionshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Quan hệ N-1 với User
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(name = "receiver_name", columnDefinition = "nvarchar(100)")
	private String receiverName;

	private String phone;
	
	@Column(columnDefinition = "nvarchar(100)")
	private String province;
	
	@Column(columnDefinition = "nvarchar(100)")

	private String district;
	
	@Column(columnDefinition = "nvarchar(100)")
	private String ward;
	
	@Column(columnDefinition = "nvarchar(100)")
	private String street;

	@Column(name = "is_default")
	private Boolean isDefault;

	public Address() {
	}

	// Getters và Setters thủ công
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}
}