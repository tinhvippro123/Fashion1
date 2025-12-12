package com.fashionshop.controller.client;

import com.fashionshop.dto.UserRegisterDTO;
import com.fashionshop.model.Category;
import com.fashionshop.model.Product;
import com.fashionshop.service.CategoryService;
import com.fashionshop.service.ProductService;
import com.fashionshop.service.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClientController {

	@Autowired
	private ProductService productService;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private UserService userService;

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
	        // THÊM 2 THAM SỐ NÀY (Có giá trị mặc định để tránh lỗi null)
	        @RequestParam(name = "minPrice", defaultValue = "0") Double minPrice,
	        @RequestParam(name = "maxPrice", defaultValue = "10000000") Double maxPrice,
	        Model model) {

	    Pageable pageable = PageRequest.of(page, 24);
	    Page<Product> productPage;
	    String breadcrumbTitle = "";

	    // --- LOGIC GỌI SERVICE (TRUYỀN THÊM minPrice) ---
	    // Giả sử hàm filterProducts của bạn thứ tự là: (catId, sizes, colors, minPrice, maxPrice, pageable)
	    
	    if (slug.equals("hang-nam-moi-ve")) {
	        Category catNam = categoryService.findBySlug("nam");
	        if (catNam != null) {
	            // Truyền minPrice vào vị trí tham số tương ứng
	            productPage = productService.filterProducts(catNam.getId(), sizes, null, minPrice, maxPrice, pageable);
	        } else {
	            productPage = Page.empty();
	        }
	        breadcrumbTitle = "HÀNG NAM MỚI VỀ";

	    } else if (slug.equals("hang-nu-moi-ve")) {
	        Category catNu = categoryService.findBySlug("nu");
	        if (catNu != null) {
	            productPage = productService.filterProducts(catNu.getId(), sizes, null, minPrice, maxPrice, pageable);
	        } else {
	            productPage = Page.empty();
	        }
	        breadcrumbTitle = "HÀNG NỮ MỚI VỀ";

	    } else {
	        Category category = categoryService.findBySlug(slug);
	        if (category == null) return "redirect:/";

	        productPage = productService.filterProducts(category.getId(), sizes, null, minPrice, maxPrice, pageable);
	        breadcrumbTitle = category.getName().toUpperCase();
	    }

	    // 3. GỬI DỮ LIỆU RA FILE HTML
	    model.addAttribute("products", productPage.getContent());
	    model.addAttribute("totalPages", productPage.getTotalPages());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("breadcrumb", breadcrumbTitle);
	    model.addAttribute("currentSlug", slug);

	    model.addAttribute("selectedSizes", sizes);
	    
	    // GỬI LẠI GIÁ TRỊ ĐỂ SLIDER HIỂN THỊ ĐÚNG VỊ TRÍ CŨ
	    model.addAttribute("selectedMinPrice", minPrice);
	    model.addAttribute("selectedMaxPrice", maxPrice);

	    return "client/products";
	}

	@GetMapping("/login")
	public String loginPage(
	        // Hứng tham số 'error' từ URL
	        @RequestParam(value = "error", required = false) String error, 
	        Model model) {
	    
	    // Nếu URL có ?error=true -> Gửi thông báo lỗi ra View
	    if (error != null) {
	        model.addAttribute("loginError", "Sai email hoặc mật khẩu! Vui lòng kiểm tra lại.");
	    }

	    return "client/login";
	}

	// Hiển thị trang Đăng ký
	@GetMapping("/register")
	public String registerPage(Model model) {
		// Gửi một object rỗng sang để form điền vào
		model.addAttribute("userDto", new UserRegisterDTO());
		return "client/register";
	}

	// Xử lý khi bấm nút Đăng ký (POST)
	@PostMapping("/register")
	public String handleRegister(@ModelAttribute("userDto") UserRegisterDTO userDto,
			RedirectAttributes redirectAttributes) {
		try {
			// Controller không xử lý logic, đẩy hết sang Service
			userService.registerUser(userDto);

			// Nếu thành công -> Chuyển về Login
			redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng đăng nhập.");
			return "redirect:/login";

		} catch (Exception e) {
			// Nếu có lỗi (vd: trùng email) -> Quay lại trang Register báo lỗi
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "Đăng ký thất bại: " + e.getMessage());
			return "redirect:/register";
		}
	}


}