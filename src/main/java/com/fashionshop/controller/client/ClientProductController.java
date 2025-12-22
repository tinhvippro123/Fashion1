package com.fashionshop.controller.client;

import com.fashionshop.model.Product;
import com.fashionshop.model.ProductColor;
import com.fashionshop.service.ProductService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClientProductController {

	@Autowired
	private ProductService productService;

	// Đây là cái bạn đang thiếu: Xử lý link /product/{id}
	@GetMapping("/product/{id}")
	public String productDetail(@PathVariable Long id,
			@RequestParam(name = "color", required = false) String selectedColorName, Model model) {

		// GỌI HÀM MỚI: Service đã lo hết việc lọc màu active rồi
		Product product = productService.getProductWithActiveColors(id);

		// Kiểm tra null (Do service trả về null nếu sp ẩn hoặc hết màu)
		if (product == null) {
			return "redirect:/";
		}

		// Logic chọn màu hiển thị (View Logic) thì vẫn để ở Controller là hợp lý
		ProductColor selectedColor = product.getProductColors().get(0);

		if (selectedColorName != null && !selectedColorName.isEmpty()) {
			for (ProductColor pc : product.getProductColors()) {
				if (pc.getColor().getName().equalsIgnoreCase(selectedColorName)) {
					selectedColor = pc;
					break;
				}
			}
		}

		model.addAttribute("product", product);
		model.addAttribute("selectedColor", selectedColor);

		return "client/product-detail";
	}

	// TÌM KIẾM + LỌC
	@GetMapping("/search")
	public String search(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", required = false) List<String> sizes,
			@RequestParam(name = "color", required = false) List<String> colors,
			@RequestParam(name = "minPrice", required = false) Double minPrice,
			@RequestParam(name = "maxPrice", required = false) Double maxPrice, Model model) {

		Pageable pageable = PageRequest.of(page, 24);

		// Xử lý list rỗng -> null để Query hoạt động đúng
		List<String> sizeParam = (sizes != null && !sizes.isEmpty()) ? sizes : null;
		List<String> colorParam = (colors != null && !colors.isEmpty()) ? colors : null;
		// Gọi hàm Vạn Năng: CategoryID để null vì đang tìm toàn sàn
		Page<Product> productPage = productService.searchProductsWithFilters(keyword, null, sizeParam, colorParam,
				minPrice, maxPrice, pageable);

		// Gửi dữ liệu ra View
		model.addAttribute("products", productPage.getContent());
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("currentPage", page);

		// Setup giao diện
		model.addAttribute("breadcrumb", "KẾT QUẢ: " + (keyword != null ? keyword.toUpperCase() : ""));
		model.addAttribute("keyword", keyword); // Quan trọng để giữ ô input và form action
		model.addAttribute("currentSlug", "search");

		// Gửi lại bộ lọc để giữ trạng thái
		model.addAttribute("selectedSizes", sizes);
		model.addAttribute("selectedColors", colors);
		model.addAttribute("selectedMinPrice", minPrice);
		model.addAttribute("selectedMaxPrice", maxPrice);

		return "client/products";
	}

}