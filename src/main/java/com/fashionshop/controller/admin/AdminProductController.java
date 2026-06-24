package com.fashionshop.controller.admin;

import com.fashionshop.model.Product;
import com.fashionshop.model.Variant;
import com.fashionshop.service.CategoryService;
import com.fashionshop.service.ColorService;
import com.fashionshop.service.ProductService;
import com.fashionshop.service.SizeService;
import com.fashionshop.service.StorageService;
import com.fashionshop.enums.VariantStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ColorService colorService;
	@Autowired
	private SizeService sizeService;
	@Autowired
	private StorageService storageService;

	@GetMapping
	public String list(@RequestParam(defaultValue = "0") int page, 
	                   @RequestParam(value = "keyword", required = false) String keyword, 
	                   Model model) {
		Page<Product> productPage;
		if (keyword != null && !keyword.isEmpty()) {
			productPage = productService.searchAdminProducts(keyword, PageRequest.of(page, 10));
			model.addAttribute("keyword", keyword);
		} else {
			productPage = productService.getAllProducts(PageRequest.of(page, 10));
		}
		model.addAttribute("products", productPage.getContent());
		model.addAttribute("page", productPage);
		return "admin/product/list";
	}

	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/product/form";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("product", productService.getProductById(id));
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/product/form";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute("product") Product product) {
		productService.saveProduct(product);
		return "redirect:/admin/products";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		productService.deleteProduct(id);
		return "redirect:/admin/products";
	}

	@GetMapping("/variants/{productId}")
	public String manageVariants(@PathVariable Long productId, Model model) {
		Product product = productService.getProductById(productId);
		model.addAttribute("product", product);

		model.addAttribute("colors", colorService.getAllColors());
		model.addAttribute("sizes", sizeService.getAllSizes());

		return "admin/product/variants";
	}

	@PostMapping("/variants/add-color")
	public String addColor(@RequestParam("productId") Long productId, @RequestParam("colorId") Long colorId) {
		productService.addColorToProduct(productId, colorId);
		return "redirect:/admin/products/variants/" + productId;
	}

	@PostMapping("/variants/add-size")
	public String addSize(@RequestParam("productId") Long productId,
			@RequestParam("productColorId") Long productColorId, @RequestParam("sizeId") Long sizeId,
			@RequestParam("price") Double price, @RequestParam("stock") Integer stock) {
		productService.addVariantToProductColor(productColorId, sizeId, price, stock);
		return "redirect:/admin/products/variants/" + productId + "#color-section-" + productColorId;
	}

	@PostMapping("/variants/upload-image")
	public String uploadImage(@RequestParam("productId") Long productId,
			@RequestParam("productColorId") Long productColorId, @RequestParam("imageFiles") MultipartFile[] imageFiles) {
		
		// Sử dụng CompletableFuture để upload ảnh song song lên Cloudinary (giảm thời gian chờ)
		java.util.List<java.util.concurrent.CompletableFuture<String>> futures = new java.util.ArrayList<>();
		for (MultipartFile file : imageFiles) {
			if (!file.isEmpty()) {
				futures.add(java.util.concurrent.CompletableFuture.supplyAsync(() -> {
					return storageService.store(file);
				}));
			}
		}

		// Đợi tất cả các file upload xong
		java.util.List<String> fileNames = futures.stream()
				.map(java.util.concurrent.CompletableFuture::join)
				.collect(java.util.stream.Collectors.toList());

		// Lưu vào database tuần tự để đảm bảo tính toàn vẹn dữ liệu
		for (String fileName : fileNames) {
			productService.addImageToProductColor(productColorId, fileName);
		}

		return "redirect:/admin/products/variants/" + productId + "#color-section-" + productColorId;
	}

	@GetMapping("/variants/delete-size/{id}")
	public String deleteVariant(@PathVariable Long id, @RequestParam Long productId, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
		try {
			productService.deleteVariant(id);
			redirectAttributes.addFlashAttribute("successMessage", "Xóa size thành công!");
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: Không thể xóa Size này vì đang nằm trong Giỏ hàng hoặc Đơn hàng của khách! Vui lòng chuyển trạng thái sang ẨN hoặc HẾT HÀNG.");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Lỗi không xác định khi xóa Size!");
		}
		return "redirect:/admin/products/variants/" + productId;
	}

	@GetMapping("/variants/delete-image/{id}")
	public String deleteImage(@PathVariable Long id, @RequestParam Long productId) {
		productService.deleteVariantImage(id);
		return "redirect:/admin/products/variants/" + productId;
	}

	@GetMapping("/variants/delete-color/{id}")
	public String deleteColorGroup(@PathVariable Long id, @RequestParam Long productId) {
		productService.deleteProductColor(id);
		return "redirect:/admin/products/variants/" + productId;
	}

	@GetMapping("/variants/edit-size/{id}")
	public String editVariantForm(@PathVariable Long id, Model model) {
		Variant variant = productService.getVariantById(id);
		model.addAttribute("variant", variant);
		return "admin/product/variant-form";
	}

	@PostMapping("/variants/update-size")
	public String updateVariant(@RequestParam("variantId") Long variantId, @RequestParam("price") Double price,
			@RequestParam("stock") Integer stock, @RequestParam("status") VariantStatus status,
			@RequestParam("productId") Long productId) {
		productService.updateVariant(variantId, price, stock, status);
		return "redirect:/admin/products/variants/" + productId;
	}

	@GetMapping("/variants/toggle-color/{id}")
	public String toggleColorStatus(@PathVariable Long id, @RequestParam Long productId) {
		productService.toggleProductColorStatus(id);
		return "redirect:/admin/products/variants/" + productId;
	}
	
	
	@GetMapping("/variants/set-default/{id}")
	public String setDefaultColor(@PathVariable Long id, @RequestParam Long productId) {
	    productService.setDefaultColor(productId, id); 
	    return "redirect:/admin/products/variants/" + productId;
	}

	@GetMapping("/variants/set-image-type/{imageId}")
	public String setImageType(@PathVariable Long imageId, @RequestParam("type") String type, @RequestParam("productId") Long productId) {
		productService.setImageType(imageId, com.fashionshop.enums.ProductImageType.valueOf(type));
		return "redirect:/admin/products/variants/" + productId;
	}
}