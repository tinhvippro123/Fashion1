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
            @RequestParam(name = "size", required = false) List<String> sizes,
            @RequestParam(name = "color", required = false) List<String> colors,
            // 1. SỬA GIÁ: Bỏ defaultValue để nhận null
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice) {
        
        // Xác định ID danh mục (Nam/Nữ)
        Long categoryId = gender.equals("men") ? 1L : 4L;
        String breadcrumbName = gender.equals("men") ? "HÀNG NAM MỚI VỀ" : "HÀNG NỮ MỚI VỀ";

        int pageSize = 24;
        Pageable pageable = PageRequest.of(page, pageSize);

        // 2. XỬ LÝ LIST RỖNG -> NULL (Cho cả Size và Color)
        List<String> sizeParam = (sizes != null && !sizes.isEmpty()) ? sizes : null;
        List<String> colorParam = (colors != null && !colors.isEmpty()) ? colors : null;

        // 3. GỌI HÀM VẠN NĂNG (keyword = null)
        Page<Product> productPage = productService.searchProductsWithFilters(
                null,           // keyword
                categoryId,     // categoryId
                sizeParam,      // sizes
                colorParam,     // colors
                minPrice,       // minPrice
                maxPrice,       // maxPrice
                pageable);

        // 4. GỬI DỮ LIỆU RA VIEW
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("gender", gender);
        model.addAttribute("breadcrumb", breadcrumbName);

        // Gửi lại bộ lọc để giữ trạng thái checkbox/slider
        model.addAttribute("selectedSizes", sizes);
        model.addAttribute("selectedColors", colors);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);

        // 5. MẸO XỬ LÝ SLUG (QUAN TRỌNG)
        // Vì HTML form đang action về: '/danh-muc/' + ${currentSlug}
        // Nên ta dùng mẹo "../new-arrival" để khi ghép chuỗi nó thành "/danh-muc/../new-arrival"
        // Trình duyệt sẽ tự hiểu là quay về "/new-arrival"
        model.addAttribute("currentSlug", "../new-arrival?gender=" + gender);

        return "client/products";
    }

	@GetMapping("/danh-muc/{slug}")
    public String categoryPage(@PathVariable("slug") String slug,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) List<String> sizes,
            @RequestParam(name = "color", required = false) List<String> colors,
            // XÓA defaultValue ĐỂ NHẬN NULL
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            Model model) {

        Pageable pageable = PageRequest.of(page, 24);
        
        // Xử lý list rỗng -> null
        List<String> sizeParam = (sizes != null && !sizes.isEmpty()) ? sizes : null;
     // --- XỬ LÝ MÀU: Rỗng -> Null ---
        List<String> colorParam = (colors != null && !colors.isEmpty()) ? colors : null;
        
        Page<Product> productPage;
        Category category = null;
        String breadcrumbTitle = "";

        // Logic tìm Category ID
        if (slug.equals("hang-nam-moi-ve")) {
            category = categoryService.findBySlug("nam");
            breadcrumbTitle = "HÀNG NAM MỚI VỀ";
        } else if (slug.equals("hang-nu-moi-ve")) {
            category = categoryService.findBySlug("nu");
            breadcrumbTitle = "HÀNG NỮ MỚI VỀ";
        } else {
            category = categoryService.findBySlug(slug);
            if (category != null) breadcrumbTitle = category.getName().toUpperCase();
        }

        if (category == null) return "redirect:/";

        // Gọi hàm Vạn Năng: Keyword để null vì đang xem danh mục
        productPage = productService.searchProductsWithFilters(null, category.getId(), sizeParam, colorParam, minPrice, maxPrice, pageable);

        // Gửi dữ liệu ra View
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("breadcrumb", breadcrumbTitle);
        model.addAttribute("currentSlug", slug);

        // Gửi lại bộ lọc
        model.addAttribute("selectedSizes", sizes);
        model.addAttribute("selectedColors", colors);
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