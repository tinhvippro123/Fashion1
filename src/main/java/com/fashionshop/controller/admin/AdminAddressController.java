package com.fashionshop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fashionshop.model.Address;
import com.fashionshop.service.AddressService;
import com.fashionshop.service.UserService;

@Controller
@RequestMapping("/admin/users/{userId}/addresses")
public class AdminAddressController {

	@Autowired
	private AddressService addressService;

	@Autowired
	private UserService userService; // Cần để lấy thông tin User hiển thị lên tiêu đề nếu muốn

	// 1. Hiển thị danh sách địa chỉ của một User cụ thể
	// URL: /users/1/addresses
	@GetMapping
	public String listAddresses(@PathVariable("userId") Long userId, Model model) {
		// Lấy danh sách địa chỉ từ Service
		model.addAttribute("addresses", addressService.getAddressesByUserId(userId));
		model.addAttribute("userId", userId); // Truyền userId xuống view để dùng cho các nút Back hoặc Add New
		model.addAttribute("user", userService.getUserById(userId)); // Để hiển thị tên user: "Địa chỉ của Nguyễn Văn A"
		return "admin/address/list"; // Trả về file templates/address/list.html
	}

	// 2. Form thêm mới địa chỉ cho User
	// URL: /users/1/addresses/new
	@GetMapping("/new")
	public String createAddressForm(@PathVariable("userId") Long userId, Model model) {
		Address address = new Address();
		model.addAttribute("address", address);
		model.addAttribute("userId", userId);
		return "admin/address/create"; // Trả về file templates/address/create.html
	}

	// 3. Xử lý lưu địa chỉ mới
	@PostMapping
	public String saveAddress(@PathVariable("userId") Long userId, @ModelAttribute("address") Address address) {
		// Gọi Service để xử lý logic gán User cho Address
		addressService.addAddressToUser(userId, address);

		// Redirect về lại trang danh sách địa chỉ của user đó
		return "redirect:/admin/users/" + userId + "/addresses";
	}

	// 4. Xóa địa chỉ
	// URL: /users/1/addresses/delete/5
	@GetMapping("/delete/{addressId}")
	public String deleteAddress(@PathVariable("userId") Long userId, @PathVariable("addressId") Long addressId) {
		addressService.deleteAddress(addressId);

		// Redirect về lại trang danh sách
		return "redirect:/admin/users/" + userId + "/addresses";
	}

	// Form sửa địa chỉ
	@GetMapping("/edit/{addressId}")
	public String editAddressForm(@PathVariable("userId") Long userId, @PathVariable("addressId") Long addressId,
			Model model) {
		model.addAttribute("address", addressService.getAddressById(addressId));
		model.addAttribute("userId", userId);
		return "admin/address/edit";
	}

	// Xử lý update
	@PostMapping("/update/{addressId}")
	public String updateAddress(@PathVariable("userId") Long userId, @PathVariable("addressId") Long addressId,
			@ModelAttribute("address") Address address) {
		addressService.updateAddress(addressId, address);
		return "redirect:/admin/users/" + userId + "/addresses";
	}
}