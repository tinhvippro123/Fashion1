package com.fashionshop.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fashionshop.service.UserService;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

	@Autowired
	private UserService userService;

	@GetMapping
	public String myProfile(Model model) {
		// Sau này dùng Security để lấy ID người đang đăng nhập
		// Tạm thời fix cứng ID = 1 để test giao diện
		Long currentUserId = 1L;

		model.addAttribute("user", userService.getUserById(currentUserId));
		return "client/profile"; // Trả về templates/client/profile.html
	}

	// User tự đổi mật khẩu, cập nhật địa chỉ... viết tại đây
}
