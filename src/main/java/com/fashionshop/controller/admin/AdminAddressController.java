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
	private UserService userService;

	// 1. Hiển thị danh sách địa chỉ của một User cụ thể
	@GetMapping
	public String listAddresses(@PathVariable("userId") Long userId, Model model) {
		model.addAttribute("addresses", addressService.getAddressesByUserId(userId));
		model.addAttribute("userId", userId);
		model.addAttribute("user", userService.getUserById(userId));
		return "admin/address/list";
	}

	@GetMapping("/new")
	public String createAddressForm(@PathVariable("userId") Long userId, Model model) {
		Address address = new Address();
		model.addAttribute("address", address);
		model.addAttribute("userId", userId);
		return "admin/address/create";
	}

	@PostMapping
	public String saveAddress(@PathVariable("userId") Long userId, @ModelAttribute("address") Address address) {
		addressService.addAddressToUser(userId, address);
		return "redirect:/admin/users/" + userId + "/addresses";
	}

	@GetMapping("/delete/{addressId}")
	public String deleteAddress(@PathVariable("userId") Long userId, @PathVariable("addressId") Long addressId) {
		addressService.deleteAddress(addressId);
		return "redirect:/admin/users/" + userId + "/addresses";
	}

	@GetMapping("/edit/{addressId}")
	public String editAddressForm(@PathVariable("userId") Long userId, @PathVariable("addressId") Long addressId,
			Model model) {
		model.addAttribute("address", addressService.getAddressById(addressId));
		model.addAttribute("userId", userId);
		return "admin/address/edit";
	}

	@PostMapping("/update/{addressId}")
	public String updateAddress(@PathVariable("userId") Long userId, @PathVariable("addressId") Long addressId,
			@ModelAttribute("address") Address address) {
		addressService.updateAddress(addressId, address);
		return "redirect:/admin/users/" + userId + "/addresses";
	}
}