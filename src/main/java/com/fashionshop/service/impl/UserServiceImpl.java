package com.fashionshop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Cần thiết vì có Spring Security
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fashionshop.model.Address;
import com.fashionshop.model.User;
import com.fashionshop.repository.AddressRepository;
import com.fashionshop.repository.UserRepository;
import com.fashionshop.service.UserService;
import com.fashionshop.dto.UserRegisterDTO;
import com.fashionshop.enums.UserGender;
import com.fashionshop.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AddressRepository addressRepository;

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
	}

	@Override
	public User createUser(User user) {
//		 Logic nghiệp vụ: Mã hóa mật khẩu
		user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

//		 Logic nghiệp vụ: Set thời gian tạo
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());

//		 Nếu chưa có role, mặc định là CUSTOMER
		if (user.getRole() == null) {
			user.setRole(UserRole.CUSTOMER);
		}

		// Mặc định user mới có thể là active
		if (user.getIsActive() == null)
			user.setIsActive(true);

		return userRepository.save(user);
	}

	@Override
	public User updateUser(Long id, User userDetails) {
		User existingUser = getUserById(id);

		// Logic update: Chỉ update các trường cho phép
		existingUser.setFullName(userDetails.getFullName());
		existingUser.setPhone(userDetails.getPhone());
		existingUser.setGender(userDetails.getGender());
		existingUser.setDateOfBirth(userDetails.getDateOfBirth());
		existingUser.setRole(userDetails.getRole());

		// Logic thời gian update
		existingUser.setUpdatedAt(LocalDateTime.now());

		return userRepository.save(existingUser);
	}

	@Override
	public void deleteUser(Long id) {
		// Logic: Có thể là xóa mềm (soft delete) bằng cách set isActive = false
		// Ở đây demo xóa cứng
		userRepository.deleteById(id);
	}

	@Override
	public User findByEmail(String email) {
		// Repository tìm theo cột email trong database
		return userRepository.findByEmail(email);
	}

	@Override
	@Transactional
	public void registerUser(UserRegisterDTO dto) {

		// 1. Kiểm tra Email
		if (userRepository.findByEmail(dto.getEmail()) != null) {
			throw new RuntimeException("Email đã tồn tại!");
		}

		// 2. Map DTO -> USER Entity
		// 2. Map DTO -> USER Entity
		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

		String fullName = dto.getLastName() + " " + dto.getFirstName();
		user.setFullName(fullName.trim());
		user.setPhone(dto.getPhoneNumber());

		// --- XỬ LÝ GIỚI TÍNH (STRING -> ENUM) ---
		// Form HTML gửi lên value="nam", "nu" hoặc "male", "female"
		// Ta cần chuẩn hóa về MALE / FEMALE
		if (dto.getGender() != null) {
			String g = dto.getGender().trim().toUpperCase(); // Chuyển về chữ hoa
			if (g.equals("NAM") || g.equals("MALE")) {
				user.setGender(UserGender.MALE);
			} else if (g.equals("NU") || g.equals("FEMALE")) {
				user.setGender(UserGender.FEMALE);
			} else {
				user.setGender(UserGender.OTHER);
			}
		}

		if (dto.getDob() != null && !dto.getDob().isEmpty()) {
			user.setDateOfBirth(LocalDate.parse(dto.getDob()));
		}

		user.setRole(UserRole.CUSTOMER); // Role mới là CUSTOMER
		user.setIsActive(true);

		// Lưu User
		User savedUser = userRepository.save(user);

//		Map DTO -> ADDRESS Entity
		Address address = new Address();
		address.setUser(savedUser);
		address.setReceiverName(savedUser.getFullName());
		address.setPhone(savedUser.getPhone());
		address.setProvince(dto.getCity());
		address.setDistrict(dto.getDistrict());
		address.setWard(dto.getWard());
		address.setStreet(dto.getDetailAddress());
		address.setIsDefault(true);

		addressRepository.save(address);
	}

	@Override
	public boolean checkPassword(User user, String rawPassword) {
		return passwordEncoder.matches(rawPassword, user.getPasswordHash());
	}

	@Override
	@Transactional
	public void changePassword(User user, String newPassword) {
		user.setPasswordHash(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	@Override
	@Transactional
	public void updateProfile(User user, String newEmail, String genderStr) {
		// 1. Cập nhật Email
		if (newEmail != null && !newEmail.isEmpty()) {
			user.setEmail(newEmail);
		}

		// 2. Cập nhật Giới tính (Convert từ String sang Enum)
		if (genderStr != null) {
			try {
				// Chuyển "male", "female" thành Enum
				UserGender gender = UserGender.valueOf(genderStr.toUpperCase());
				user.setGender(gender);
			} catch (IllegalArgumentException e) {
				// Nếu giá trị không hợp lệ thì bỏ qua hoặc set OTHER
				user.setGender(UserGender.OTHER);
			}
		}

		// 3. Lưu xuống DB
		userRepository.save(user);
	}

	public long countAllCustomers() {
		return userRepository.countByRole(UserRole.CUSTOMER);
	}

}