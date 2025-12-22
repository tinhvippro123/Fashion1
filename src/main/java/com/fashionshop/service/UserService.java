package com.fashionshop.service;

import java.util.List;

import com.fashionshop.dto.UserRegisterDTO;
import com.fashionshop.model.User;

public interface UserService {
	List<User> getAllUsers();

	User getUserById(Long id);

	User createUser(com.fashionshop.model.User user);

	User updateUser(Long id, User user);

	void deleteUser(Long id);

	User findByEmail(String email);

	void registerUser(UserRegisterDTO registrationDto);

	void changePassword(User user, String newPassword);

	boolean checkPassword(User user, String rawPassword);
	
	void updateProfile(User user, String newEmail, String genderStr);
	
	long countAllCustomers();
}