package com.fashionshop.service.impl;

import com.fashionshop.exception.FashionShopException;

import com.fashionshop.exception.ErrorCode;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder; // Cáº§n thiáº¿t vÃ¬ cÃ³ Spring Security

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

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;



@Service

public class UserServiceImpl implements UserService {



	@Autowired

	private UserRepository userRepository;



	@Autowired

	private PasswordEncoder passwordEncoder;



	@Autowired

	private AddressRepository addressRepository;



	@Override

	public Page<User> getAllUsers(Pageable pageable) {

		return userRepository.findAll(pageable);

	}



	@Override

	public Page<User> searchUsers(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {

			return userRepository.findAll(pageable);

		}

		return userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword.trim(), keyword.trim(), pageable);

	}



	@Override

	public User getUserById(Long id) {

		return userRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.UNAUTHENTICATED, "User not found"));

	}



	@Override

	public User createUser(User user) {

//		 Logic nghiá»‡p vá»¥: MÃ£ hÃ³a máº­t kháº©u

		user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));



//		 Logic nghiá»‡p vá»¥: Set thá»\ufffdi gian táº¡o

		user.setCreatedAt(LocalDateTime.now());

		user.setUpdatedAt(LocalDateTime.now());



//		 Náº¿u chÆ°a cÃ³ role, máº·c Ä‘á»‹nh lÃ  CUSTOMER

		if (user.getRole() == null) {

			user.setRole(UserRole.CUSTOMER);

		}



		// Máº·c Ä‘á»‹nh user má»›i cÃ³ thá»ƒ lÃ  active

		if (user.getIsActive() == null)

			user.setIsActive(true);



		return userRepository.save(user);

	}



	@Override

	public User updateUser(Long id, User userDetails) {

		User existingUser = getUserById(id);



		// Logic update: Chá»‰ update cÃ¡c trÆ°á»\ufffdng cho phÃ©p

		existingUser.setFullName(userDetails.getFullName());

		existingUser.setEmail(userDetails.getEmail());

		existingUser.setPhone(userDetails.getPhone());

		existingUser.setGender(userDetails.getGender());

		existingUser.setDateOfBirth(userDetails.getDateOfBirth());

		existingUser.setRole(userDetails.getRole());

		if (userDetails.getIsActive() != null) {

			existingUser.setIsActive(userDetails.getIsActive());

		}



		// Logic thá»\ufffdi gian update

		existingUser.setUpdatedAt(LocalDateTime.now());



		return userRepository.save(existingUser);

	}



	@Override

	public void deleteUser(Long id) {

		// Logic: CÃ³ thá»ƒ lÃ  xÃ³a má»\ufffdm (soft delete) báº±ng cÃ¡ch set isActive = false

		// á»ž Ä‘Ã¢y demo xÃ³a cá»©ng

		userRepository.deleteById(id);

	}



	@Override

	public User findByEmail(String email) {

		return userRepository.findByEmail(email);

	}



	@Override

	public User getUserByEmailOrThrow(String email) {

		User user = userRepository.findByEmail(email);

		if (user == null) {

			throw new FashionShopException(ErrorCode.UNAUTHENTICATED);

		}

		return user;

	}



	@Override

	@Transactional

	public void registerUser(UserRegisterDTO dto) {



		// 1. Kiá»ƒm tra Email

		if (userRepository.findByEmail(dto.getEmail()) != null) {

			throw new FashionShopException(ErrorCode.VALIDATION_ERROR);

		}



		// 2. Kiá»ƒm tra máº­t kháº©u xÃ¡c nháº­n

		if (!dto.getPassword().equals(dto.getConfirmPassword())) {

			throw new FashionShopException(ErrorCode.VALIDATION_ERROR);

		}



		// 2. Map DTO -> USER Entity

		// 2. Map DTO -> USER Entity

		User user = new User();

		user.setEmail(dto.getEmail());

		user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));



		String fullName = dto.getLastName() + " " + dto.getFirstName();

		user.setFullName(fullName.trim());

		user.setPhone(dto.getPhoneNumber());



		// --- Xá»¬ LÃ\ufffd GIá»šI TÃ\ufffdNH (STRING -> ENUM) ---

		// Form HTML gá»­i lÃªn value="nam", "nu" hoáº·c "male", "female"

		// Ta cáº§n chuáº©n hÃ³a vá»\ufffd MALE / FEMALE

		if (dto.getGender() != null) {

			String g = dto.getGender().trim().toUpperCase(); // Chuyá»ƒn vá»\ufffd chá»¯ hoa

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



		user.setRole(UserRole.CUSTOMER); // Role má»›i lÃ  CUSTOMER

		user.setIsActive(true);



		// LÆ°u User

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

	public void changePassword(User user, String currentPassword, String newPassword, String confirmPassword) {

		if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {

			throw new FashionShopException(ErrorCode.VALIDATION_ERROR);

		}

		

		if (!newPassword.equals(confirmPassword)) {

			throw new FashionShopException(ErrorCode.VALIDATION_ERROR);

		}



		user.setPasswordHash(passwordEncoder.encode(newPassword));

		userRepository.save(user);

	}



	@Override

	@Transactional

	public void updateProfile(User user, String newEmail, String genderStr) {

		// 1. Cáº­p nháº­t Email

		if (newEmail != null && !newEmail.isEmpty() && !newEmail.equals(user.getEmail())) {

			if (userRepository.findByEmail(newEmail) != null) {

				throw new FashionShopException(ErrorCode.VALIDATION_ERROR);

			}

			user.setEmail(newEmail);

		}



		// 2. Cáº­p nháº­t Giá»›i tÃ­nh (Convert tá»« String sang Enum)

		if (genderStr != null) {

			try {

				// Chuyá»ƒn "male", "female" thÃ nh Enum

				UserGender gender = UserGender.valueOf(genderStr.toUpperCase());

				user.setGender(gender);

			} catch (IllegalArgumentException e) {

				// Náº¿u giÃ¡ trá»‹ khÃ´ng há»£p lá»‡ thÃ¬ bá»\ufffd qua hoáº·c set OTHER

				user.setGender(UserGender.OTHER);

			}

		}



		// 3. LÆ°u xuá»‘ng DB

		userRepository.save(user);

	}



    @Override

    public long countAllCustomers() {

        return userRepository.countByRole(com.fashionshop.enums.UserRole.CUSTOMER);

    }



    @Override

    public java.util.Map<String, Object> getProfileData(String email) {

        User user = getUserByEmailOrThrow(email);

        com.fashionshop.dto.UserProfileResponse profileResponse = new com.fashionshop.dto.UserProfileResponse(user);

        java.util.Map<String, Object> response = new java.util.HashMap<>();

        response.put("user", profileResponse);

        return response;

    }



    @Override

    public java.util.Map<String, Object> generateLoginResponse(String email, String token) {

        User user = findByEmail(email);

        java.util.Map<String, Object> responseData = new java.util.HashMap<>();

        responseData.put("token", token);

        responseData.put("email", user.getEmail());

        responseData.put("firstName", user.getFirstName());

        responseData.put("lastName", user.getLastName());

        return responseData;

    }



    @Override

    public void updateUserRole(Long id, com.fashionshop.enums.UserRole role) {

        User user = getUserById(id);

        user.setRole(role);

        userRepository.save(user);

    }



    @Override

    public void toggleUserStatus(Long id, Boolean isActive) {

        User user = getUserById(id);

        user.setIsActive(isActive);

        userRepository.save(user);

    }



    @Override

    public void updateProfileByEmail(String userEmail, String newEmail, String gender) {

        User user = getUserByEmailOrThrow(userEmail);

        updateProfile(user, newEmail, gender);

    }



    @Override

    public void changePasswordByEmail(String userEmail, String currentPassword, String newPassword, String confirmPassword) {

        User user = getUserByEmailOrThrow(userEmail);

        changePassword(user, currentPassword, newPassword, confirmPassword);

    }





}

