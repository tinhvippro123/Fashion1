package com.fashionshop.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // Cần thiết vì có Spring Security
import org.springframework.stereotype.Service;

import com.fashionshop.model.User;
import com.fashionshop.repository.UserRepository;
import com.fashionshop.service.UserService;
import com.fashionshop.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder; // Inject Bean BCryptPasswordEncoder từ file Config

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
		// Logic nghiệp vụ: Mã hóa mật khẩu
		user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

		// Logic nghiệp vụ: Set thời gian tạo
		user.setCreatedAt(LocalDateTime.now());
		user.setUpdatedAt(LocalDateTime.now());

		// Nếu chưa có role, mặc định là USER
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
}