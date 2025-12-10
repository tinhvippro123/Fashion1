package com.fashionshop.controller.client;

import com.fashionshop.model.Category;
import com.fashionshop.model.Product;
import com.fashionshop.service.CategoryService;
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
public class ClientController {

	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryService categoryService;

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

	@GetMapping("/new-arrival")
	public String newArrivalPage(Model model,
			@RequestParam(name = "gender", required = false, defaultValue = "women") String gender,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", required = false) List<String> sizes, // List size chọn (S, M...)
			@RequestParam(name = "color", required = false) List<String> colors, // List màu chọn
			@RequestParam(name = "minPrice", required = false) Double minPrice,
			@RequestParam(name = "maxPrice", required = false) Double maxPrice) {
		// 1. Xác định Category ID (Nam = 1, Nữ = 4 - Theo DB của bạn)
		Long categoryId = gender.equals("men") ? 1L : 4L;
		String breadcrumbName = gender.equals("men") ? "HÀNG NAM MỚI VỀ" : "HÀNG NỮ MỚI VỀ";

		// 2. Cấu hình phân trang: 24 sản phẩm / trang
		int pageSize = 24;
		Pageable pageable = PageRequest.of(page, pageSize);

		// 3. Gọi Service lọc sản phẩm (Hàm này sẽ viết ở Bước 2)
		Page<Product> productPage = productService.filterProducts(categoryId, sizes, colors, minPrice, maxPrice,
				pageable);

		// 4. Gửi dữ liệu sang View
		model.addAttribute("products", productPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("gender", gender); // Để giữ trạng thái tab
		model.addAttribute("breadcrumb", breadcrumbName);

		// Gửi lại các bộ lọc đã chọn để UI tick vào
		model.addAttribute("selectedSizes", sizes);
		model.addAttribute("selectedColors", colors);

		return "client/products"; // File HTML mới
	}

	@GetMapping("/danh-muc/{slug}")
	public String categoryPage(@PathVariable("slug") String slug,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", required = false) List<String> sizes,
			@RequestParam(name = "maxPrice", required = false) Double maxPrice, Model model) {
		// Cấu hình: 24 sản phẩm/trang (4 cột x 6 hàng)
		Pageable pageable = PageRequest.of(page, 24);
		Page<Product> productPage;
		String breadcrumbTitle = "";

		// --- LOGIC MỚI: KHÔNG DÙNG ID CỨNG ---

		if (slug.equals("hang-nam-moi-ve")) {
			// 1. Tự động tìm danh mục có slug là "nam" trong DB
			Category catNam = categoryService.findBySlug("nam"); // Slug này phải chuẩn trong DB

			if (catNam != null) {
				// Lấy ID động từ kết quả tìm được (Dù là 1, 5 hay 100 đều chạy đúng)
				productPage = productService.filterProducts(catNam.getId(), sizes, null, null, maxPrice, pageable);
			} else {
				productPage = Page.empty(); // Không tìm thấy danh mục Nam thì trả về rỗng
			}

			breadcrumbTitle = "HÀNG NAM MỚI VỀ";

		} else if (slug.equals("hang-nu-moi-ve")) {
			// 2. Tự động tìm danh mục có slug là "nu" (hoặc "nu-thoi-trang" tùy DB bạn đặt)
			Category catNu = categoryService.findBySlug("nu");

			if (catNu != null) {
				productPage = productService.filterProducts(catNu.getId(), sizes, null, null, maxPrice, pageable);
			} else {
				productPage = Page.empty();
			}

			breadcrumbTitle = "HÀNG NỮ MỚI VỀ";

		} else {
			// CASE 3: DANH MỤC THƯỜNG (Giữ nguyên)
			Category category = categoryService.findBySlug(slug);
			if (category == null)
				return "redirect:/";

			productPage = productService.filterProducts(category.getId(), sizes, null, null, maxPrice, pageable);
			breadcrumbTitle = category.getName().toUpperCase();
		}

		// 3. GỬI DỮ LIỆU RA FILE HTML
		model.addAttribute("products", productPage.getContent());
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("currentPage", page);
		model.addAttribute("breadcrumb", breadcrumbTitle);
		model.addAttribute("currentSlug", slug); // Để giữ URL khi bấm qua trang 2, 3

		// Gửi lại các bộ lọc đã chọn để tick vào checkbox
		model.addAttribute("selectedSizes", sizes);
		model.addAttribute("selectedMaxPrice", maxPrice);

		return "client/products"; // Trả về file giao diện danh sách
	}

}