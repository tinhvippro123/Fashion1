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

	@GetMapping
	public String listUsers(Model model) {
		model.addAttribute("users", userService.getAllUsers());
		return "admin/user/list";
	}

	@GetMapping("/new")
	public String createUserForm(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "admin/user/create";
	}

	@PostMapping
	public String saveUser(@ModelAttribute("user") User user) {
		userService.createUser(user);
		return "redirect:/admin/users";
	}

	@GetMapping("/edit/{id}")
	public String editUserForm(@PathVariable Long id, Model model) {
		model.addAttribute("user", userService.getUserById(id));
		return "admin/user/edit";
	}

	@PostMapping("/{id}")
	public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user) {
		userService.updateUser(id, user);
		return "redirect:/admin/users";
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return "redirect:/admin/users";
	}
}