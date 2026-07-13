package com.fashionshop.service;
import com.fashionshop.model.User;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fashionshop.dto.UserRegisterDTO;
import com.fashionshop.model.User;

public interface UserService {
	Page<User> getAllUsers(Pageable pageable);
	Page<User> searchUsers(String keyword, Pageable pageable);

	User getUserById(Long id);

	User createUser(User user);

	User updateUser(Long id, User user);

	void deleteUser(Long id);
	void updateUserRole(Long id, com.fashionshop.enums.UserRole role);
	void toggleUserStatus(Long id, Boolean isActive);

	User findByEmail(String email);
	User getUserByEmailOrThrow(String email);

	void registerUser(UserRegisterDTO registrationDto);

	void changePassword(User user, String currentPassword, String newPassword, String confirmPassword);

	boolean checkPassword(User user, String rawPassword);
	
	void updateProfile(User user, String newEmail, String genderStr);
	
	long countAllCustomers();
	
	java.util.Map<String, Object> getProfileData(String email);
	
	java.util.Map<String, Object> generateLoginResponse(String email, String token);

	void updateProfileByEmail(String userEmail, String newEmail, String gender);
	void changePasswordByEmail(String userEmail, String currentPassword, String newPassword, String confirmPassword);
}