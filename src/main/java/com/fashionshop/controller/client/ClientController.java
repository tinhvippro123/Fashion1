package com.fashionshop.controller.client;
import org.springframework.data.domain.Sort;
import com.fashionshop.service.BannerService;

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
	@Autowired
	private BannerService bannerService;

	@GetMapping(value = { "/", "/home" })
	public String home(Model model) {
		List<Product> womenProducts = productService.findTop10NewestWomen();

		List<Product> menProducts = productService.findTop10NewestMen();

		System.out.println("Sá»‘ lÆ°á»£ng Ä‘á»“ Ná»¯ tÃ¬m tháº¥y: " + womenProducts.size());
		System.out.println("Sá»‘ lÆ°á»£ng Ä‘á»“ Nam tÃ¬m tháº¥y: " + menProducts.size());

		model.addAttribute("womenProducts", womenProducts);
		model.addAttribute("menProducts", menProducts);
		model.addAttribute("banners", bannerService.getActiveBanners());
		return "client/home";
	}

	@GetMapping("/new-arrival")
    public String newArrivalPage(Model model,
            @RequestParam(name = "gender", required = false, defaultValue = "women") String gender,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) List<String> sizes,
            @RequestParam(name = "color", required = false) List<String> colors,
            // 1. Sá»¬A GIÃ\ufffd: Bá»\ufffd defaultValue Ä‘á»ƒ nháº­n null
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "sort", required = false, defaultValue = "default") String sort) {
        
        // XÃ¡c Ä‘á»‹nh ID danh má»¥c (Nam/Ná»¯)
        Long categoryId = gender.equals("men") ? 1L : 4L;
        String breadcrumbName = gender.equals("men") ? "HÃ€NG NAM Má»šI Vá»€" : "HÃ€NG Ná»® Má»šI Vá»€";

        int pageSize = 24;
        Sort sortObj = Sort.unsorted();
        if ("price_asc".equals(sort)) {
            sortObj = Sort.by("basePrice").ascending();
        } else if ("price_desc".equals(sort)) {
            sortObj = Sort.by("basePrice").descending();
        } else if ("newest".equals(sort)) {
            sortObj = Sort.by("createdAt").descending();
        }
        Pageable pageable = PageRequest.of(page, pageSize, sortObj);

        List<String> sizeParam = (sizes != null && !sizes.isEmpty()) ? sizes : null;
        List<String> colorParam = (colors != null && !colors.isEmpty()) ? colors : null;

        // 3. Gá»ŒI HÃ€M Váº N NÄ‚NG (keyword = null)
        Page<Product> productPage = productService.searchProductsWithFilters(
                null,        
                categoryId,     
                sizeParam, 
                colorParam, 
                minPrice,     
                maxPrice,     
                pageable);

        // 4. Gá»¬I Dá»® LIá»†U RA VIEW
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("gender", gender);
        model.addAttribute("breadcrumb", breadcrumbName);

        model.addAttribute("selectedSizes", sizes);
        model.addAttribute("selectedColors", colors);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);
        model.addAttribute("selectedSort", sort);

        model.addAttribute("currentSlug", "../new-arrival?gender=" + gender);

        return "client/products";
    }

	@GetMapping("/danh-muc/{slug}")
    public String categoryPage(@PathVariable("slug") String slug,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) List<String> sizes,
            @RequestParam(name = "color", required = false) List<String> colors,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
            Model model) {

        int pageSize = 24;
        Sort sortObj = Sort.unsorted();
        if ("price_asc".equals(sort)) {
            sortObj = Sort.by("basePrice").ascending();
        } else if ("price_desc".equals(sort)) {
            sortObj = Sort.by("basePrice").descending();
        } else if ("newest".equals(sort)) {
            sortObj = Sort.by("createdAt").descending();
        }
        Pageable pageable = PageRequest.of(page, pageSize, sortObj);
        
        List<String> sizeParam = (sizes != null && !sizes.isEmpty()) ? sizes : null;
        List<String> colorParam = (colors != null && !colors.isEmpty()) ? colors : null;
        
        Page<Product> productPage;
        Category category = null;
        String breadcrumbTitle = "";

        if (slug.equals("hang-nam-moi-ve")) {
            category = categoryService.findBySlug("nam");
            breadcrumbTitle = "HÃ€NG NAM Má»šI Vá»€";
        } else if (slug.equals("hang-nu-moi-ve")) {
            category = categoryService.findBySlug("nu");
            breadcrumbTitle = "HÃ€NG Ná»® Má»šI Vá»€";
        } else {
            category = categoryService.findBySlug(slug);
            if (category != null) breadcrumbTitle = category.getName().toUpperCase();
        }

        if (category == null) return "redirect:/";

        productPage = productService.searchProductsWithFilters(null, category.getId(), sizeParam, colorParam, minPrice, maxPrice, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("breadcrumb", breadcrumbTitle);
        model.addAttribute("currentSlug", slug);

        model.addAttribute("selectedSizes", sizes);
        model.addAttribute("selectedColors", colors);
        model.addAttribute("selectedMinPrice", minPrice);
        model.addAttribute("selectedMaxPrice", maxPrice);
        model.addAttribute("selectedSort", sort);

        return "client/products";
    }

	@GetMapping("/login")
	public String loginPage(
	        @RequestParam(value = "error", required = false) String error, 
	        Model model) {
	    
	    if (error != null) {
	        model.addAttribute("loginError", "Sai email hoáº·c máº­t kháº©u! Vui lÃ²ng kiá»ƒm tra láº¡i.");
	    }

	    return "client/login";
	}

	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute("userDto", new UserRegisterDTO());
		return "client/register";
	}

	@PostMapping("/register")
	public String handleRegister(@ModelAttribute("userDto") UserRegisterDTO userDto,
			RedirectAttributes redirectAttributes) {
		try {
			userService.registerUser(userDto);

			redirectAttributes.addFlashAttribute("successMessage", "Ä\ufffdÄƒng kÃ½ thÃ nh cÃ´ng! Vui lÃ²ng Ä‘Äƒng nháº­p.");
			return "redirect:/login";

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "Ä\ufffdÄƒng kÃ½ tháº¥t báº¡i: " + e.getMessage());
			return "redirect:/register";
		}
	}


}