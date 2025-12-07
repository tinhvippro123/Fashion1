package com.fashionshop.controller.client;

import com.fashionshop.model.Product;
import com.fashionshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ClientProductController {

	@Autowired
	private ProductService productService;

//	// 1. Trang chủ: Hiển thị danh sách sản phẩm mới nhất
//	@GetMapping(value = { "/", "/home" })
//	public String home(Model model) {
//		// Lấy tất cả sản phẩm đang Active (Đang bán)
//		// (Sau này bạn có thể viết thêm hàm lấy 8 sản phẩm mới nhất trong Service)
//		List<Product> products = productService.getAllProducts();
//		model.addAttribute("products", products);
//
//		return "client/home"; // File HTML trang chủ
//	}
//
//	// 2. Trang chi tiết sản phẩm: Xem màu, size, ảnh
//	@GetMapping("/product/{id}") // Thực tế nên dùng slug: /product/{slug}
//	public String productDetail(@PathVariable Long id, Model model) {
//		Product product = productService.getProductById(id);
//
//		if (product == null || !product.getIsActive()) {
//			return "redirect:/"; // Nếu sp không tồn tại hoặc ngừng bán thì về trang chủ
//		}
//
//		model.addAttribute("product", product);
//		return "client/product-detail"; // File HTML chi tiết
//	}

	// Endpoint xử lý chung cho Category
    @GetMapping("/category/{slug}")
    public String productsByCategory(@PathVariable String slug, 
                                     @RequestParam(value = "filter", required = false) String filter,
                                     Model model) {
        List<Product> products;

        if ("new-arrival".equals(filter)) {
            // Nếu bấm vào NEW ARRIVAL
            products = productService.getNewArrivalsByCategorySlug(slug);
            model.addAttribute("pageTitle", "Hàng mới về");
        } else {
            // Nếu bấm vào ALL ITEMS (hoặc tên danh mục)
            products = productService.getProductsByCategorySlug(slug);
            model.addAttribute("pageTitle", "Tất cả sản phẩm");
        }

        model.addAttribute("products", products);
        return "client/products"; // Trang danh sách sản phẩm (sẽ tạo sau)
    }

}