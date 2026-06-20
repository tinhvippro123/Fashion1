package com.fashionshop.model;

import java.time.LocalDateTime;

import com.fashionshop.enums.AddressType;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

	@Column(name = "receiver_name", columnDefinition = "varchar(100)")
	private String receiverName;

	@Column(length = 10)
	private String phone;

	@Column(columnDefinition = "varchar(100)")
	private String province;

	@Column(columnDefinition = "varchar(100)")
	private String district;

	@Column(columnDefinition = "varchar(100)")
	private String ward;

	@Column(columnDefinition = "varchar(100)")
	private String street;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Enumerated(EnumType.STRING)
	@Column(name = "address_type", length = 10)
	private AddressType addressType = AddressType.HOME;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
	
}