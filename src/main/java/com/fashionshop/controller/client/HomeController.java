package com.fashionshop.controller.client;

import com.fashionshop.model.Product;
import com.fashionshop.service.ProductService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@Autowired
	private ProductService productService;

	// Đây là hàm xử lý khi người dùng vào trang chủ (http://localhost:8080/)
	@GetMapping(value = { "/", "/home" })
	public String home(Model model) {
		// 1. Lấy list Nữ (IVY moda)
	    List<Product> womenProducts = productService.findTop10NewestWomen();
	    
	    // 2. Lấy list Nam (Metagent)
	    List<Product> menProducts = productService.findTop10NewestMen();
	    
	    System.out.println("Số lượng đồ Nữ tìm thấy: " + womenProducts.size());
        System.out.println("Số lượng đồ Nam tìm thấy: " + menProducts.size());
	    
	    model.addAttribute("womenProducts", womenProducts);
	    model.addAttribute("menProducts", menProducts);
		return "client/home";
	}
	
	
}