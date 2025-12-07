package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.User;

public interface UserService {
	List<User> getAllUsers();

	User getUserById(Long id);

	User createUser(com.fashionshop.model.User user);

	User updateUser(Long id, User user);

	void deleteUser(Long id);
}