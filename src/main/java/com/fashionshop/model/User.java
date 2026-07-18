package com.fashionshop.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fashionshop.enums.UserGender;
import com.fashionshop.enums.UserRole;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "password_hash", nullable = false, length = 255)
	private String passwordHash;

	@Column(name = "full_name", columnDefinition = "varchar(100)")
	private String fullName;

	@Column(length = 255)
	private String avatar;

	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private UserGender gender;

	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;

	@Column(length = 10)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private UserRole role;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Quan hệ 1-N với Address
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Address> addresses;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		if (this.isActive == null)
			this.isActive = true;
		if (this.role == null)
			this.role = UserRole.CUSTOMER;
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	public String getLastName() {
		if (fullName != null && fullName.contains(" ")) {
			return fullName.substring(0, fullName.lastIndexOf(" "));
		}
		return "";
	}

	public String getFirstName() {
		if (fullName != null && fullName.contains(" ")) {
			return fullName.substring(fullName.lastIndexOf(" ") + 1);
		}
		return fullName;
	}
}
