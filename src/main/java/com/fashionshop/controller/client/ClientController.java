package com.fashionshop.controller.client;

import com.fashionshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ClientController {

	@Autowired
	private ProductService productService;

	// Đây là hàm xử lý khi người dùng vào trang chủ (http://localhost:8080/)
	@GetMapping(value = { "/", "/home" })
	public String home(Model model) {
		// Lấy danh sách sản phẩm để hiển thị (tạm thời lấy tất cả)
		model.addAttribute("products", productService.getAllProducts());

			// Trả về file home.html nằm trong thư mục templates/client/
		return "client/home";
	}
}