package com.fashionshop.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fashionshop.model.User;
import com.fashionshop.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

	@Autowired
	private UserService userService;

	// Hiển thị danh sách user
	@GetMapping
	public String listUsers(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "admin/user/list"; // Trả về file templates/user/list.html
	}

	// Form thêm mới
	@GetMapping("/new")
	public String createUserForm(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "admin/user/create"; // Trả về file templates/user/create.html
	}

	// Xử lý submit form thêm mới
	@PostMapping
	public String saveUser(@ModelAttribute("user") User user) {
		userService.createUser(user); // Gọi service xử lý logic
		return "redirect:/admin/users";
	}

	// Form cập nhật
	@GetMapping("/edit/{id}")
	public String editUserForm(@PathVariable Long id, Model model) {
		model.addAttribute("user", userService.getUserById(id));
		return "admin/user/edit";
	}

	// Xử lý submit form cập nhật
	@PostMapping("/{id}")
	public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user) {
		userService.updateUser(id, user); // Gọi service xử lý logic
		return "redirect:/admin/users";
	}

	// Xóa user
	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "redirect:/admin/users";
	}
}