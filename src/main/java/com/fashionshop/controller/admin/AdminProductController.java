package com.fashionshop.controller.admin;

import com.fashionshop.model.Product;
import com.fashionshop.model.Variant;
import com.fashionshop.service.CategoryService;
import com.fashionshop.service.ColorService;
import com.fashionshop.service.ProductService;
import com.fashionshop.service.SizeService;
import com.fashionshop.service.StorageService;
import com.fashionshope.enums.VariantStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

	// 1. Danh sách sản phẩm
	@GetMapping
	public String list(Model model) {
		model.addAttribute("products", productService.getAllProducts());
		return "admin/product/list";
	}

	// 2. Form thêm mới
	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("product", new Product());
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/product/form";
	}

	// 3. Form sửa
	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("product", productService.getProductById(id));
		model.addAttribute("categories", categoryService.getAllCategories());
		return "admin/product/form";
	}

	// 4. Lưu sản phẩm (Chỉ thông tin cơ bản)
	@PostMapping("/save")
	public String save(@ModelAttribute("product") Product product) {
		productService.saveProduct(product);
		// Lưu xong chuyển hướng về danh sách (hoặc về trang cấu hình chi tiết sau này)
		return "redirect:/admin/products";
	}

	// 5. Xóa sản phẩm
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		productService.deleteProduct(id);
		return "redirect:/admin/products";
	}

	// 1. Trang Quản lý Biến thể (Giao diện chính)
	@GetMapping("/variants/{productId}")
	public String manageVariants(@PathVariable Long productId, Model model) {
		Product product = productService.getProductById(productId);
		model.addAttribute("product", product);

		// Gửi danh sách màu và size để hiển thị trong dropdown chọn
		model.addAttribute("colors", colorService.getAllColors());
		model.addAttribute("sizes", sizeService.getAllSizes());

		return "admin/product/variants"; // File HTML chúng ta sắp tạo
	}

	// 2. Xử lý: Thêm Màu cho Sản phẩm
	@PostMapping("/variants/add-color")
	public String addColor(@RequestParam("productId") Long productId, @RequestParam("colorId") Long colorId) {
		productService.addColorToProduct(productId, colorId);
		return "redirect:/admin/products/variants/" + productId;
	}

	// 3. Xử lý: Thêm Size (Variant) cho một Màu cụ thể
	@PostMapping("/variants/add-size")
	public String addSize(@RequestParam("productId") Long productId,
			@RequestParam("productColorId") Long productColorId, @RequestParam("sizeId") Long sizeId,
			@RequestParam("price") Double price, @RequestParam("stock") Integer stock) {
		productService.addVariantToProductColor(productColorId, sizeId, price, stock);
		return "redirect:/admin/products/variants/" + productId;
	}

	// 4. Xử lý: Upload Ảnh cho một Màu cụ thể
	@PostMapping("/variants/upload-image")
	public String uploadImage(@RequestParam("productId") Long productId,
			@RequestParam("productColorId") Long productColorId, @RequestParam("imageFile") MultipartFile file) {
		if (!file.isEmpty()) {
			// 1. Lưu file vào ổ cứng
			String fileName = storageService.store(file);
			// 2. Lưu tên file vào DB
			productService.addImageToProductColor(productColorId, fileName);
		}
		return "redirect:/admin/products/variants/" + productId;
	}

	// 5. Xóa một Size (Variant) cụ thể
	@GetMapping("/variants/delete-size/{id}")
	public String deleteVariant(@PathVariable Long id, @RequestParam Long productId) {
		productService.deleteVariant(id);
		return "redirect:/admin/products/variants/" + productId;
	}

	// 6. Xóa một Ảnh cụ thể
	@GetMapping("/variants/delete-image/{id}")
	public String deleteImage(@PathVariable Long id, @RequestParam Long productId) {
		productService.deleteVariantImage(id);
		return "redirect:/admin/products/variants/" + productId;
	}

	// 7. Xóa cả nhóm Màu (Xóa ProductColor)
	@GetMapping("/variants/delete-color/{id}")
	public String deleteColorGroup(@PathVariable Long id, @RequestParam Long productId) {
		productService.deleteProductColor(id);
		return "redirect:/admin/products/variants/" + productId;
	}

	// 8. Hiển thị Form Sửa Variant (Size/Giá/Kho)
	@GetMapping("/variants/edit-size/{id}")
	public String editVariantForm(@PathVariable Long id, Model model) {
		Variant variant = productService.getVariantById(id);
		model.addAttribute("variant", variant);
		return "admin/product/variant-form"; // Chúng ta sẽ tạo file này
	}

	// 9. Xử lý Lưu sau khi Sửa
	@PostMapping("/variants/update-size")
	public String updateVariant(@RequestParam("variantId") Long variantId, @RequestParam("price") Double price,
			@RequestParam("stock") Integer stock, @RequestParam("status") VariantStatus status,
			@RequestParam("productId") Long productId) {
		productService.updateVariant(variantId, price, stock, status);
		return "redirect:/admin/products/variants/" + productId;
	}

}