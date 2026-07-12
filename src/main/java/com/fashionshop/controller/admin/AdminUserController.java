package com.fashionshop.controller.admin;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;

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
	public String list(@RequestParam(defaultValue = "0") int page, 
	                   @RequestParam(value = "keyword", required = false) String keyword, 
	                   Model model) {
		Page<User> userPage;
		if (keyword != null && !keyword.isEmpty()) {
			userPage = userService.searchUsers(keyword, PageRequest.of(page, 10));
			model.addAttribute("keyword", keyword);
		} else {
			userPage = userService.getAllUsers(PageRequest.of(page, 10));
		}
		model.addAttribute("users", userPage.getContent());
		model.addAttribute("page", userPage);
		return "admin/user/list";
	}

	@GetMapping("/new")
	public String createUserForm(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "admin/user/form";
	}

	@PostMapping
	public String saveUser(@ModelAttribute("user") User user) {
		userService.createUser(user);
		return "redirect:/admin/users";
	}

	@GetMapping("/edit/{id}")
	public String editUserForm(@PathVariable Long id, Model model) {
		model.addAttribute("user", userService.getUserById(id));
		return "admin/user/form";
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