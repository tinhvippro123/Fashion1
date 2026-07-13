package com.fashionshop.controller.api.admin;
import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.admin.ProductRequestDTO;
import com.fashionshop.enums.ProductImageType;
import com.fashionshop.model.Product;
import com.fashionshop.model.Variant;
import com.fashionshop.service.ProductService;
import com.fashionshop.service.StorageService;
import com.fashionshop.enums.VariantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private StorageService storageService;

    @GetMapping
    public ApiResponse<Page<Product>> list(@RequestParam(defaultValue = "0") int page, 
                                           @RequestParam(value = "keyword", required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return ApiResponse.success(productService.searchAdminProducts(keyword, PageRequest.of(page, 10)));
        } else {
            return ApiResponse.success(productService.getAllProducts(PageRequest.of(page, 10)));
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> getProduct(@PathVariable Long id) {
        return ApiResponse.success(productService.getProductById(id));
    }

    @PostMapping
    public ApiResponse<Product> create(@Valid @RequestBody ProductRequestDTO request) {
        return ApiResponse.success(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Product> update(@PathVariable Long id, @Valid @RequestBody ProductRequestDTO request) {
        return ApiResponse.success(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("XÃ³a sáº£n pháº©m thÃ nh cÃ´ng");
    }

    // --- VARIANT MANAGEMENT ---
    @PostMapping("/{productId}/colors")
    public ApiResponse<String> addColor(@PathVariable Long productId, @RequestParam Long colorId) {
        productService.addColorToProduct(productId, colorId);
        return ApiResponse.success("ThÃªm mÃ u sáº¯c thÃ nh cÃ´ng");
    }

    @PostMapping("/colors/{productColorId}/sizes")
    public ApiResponse<String> addSize(@PathVariable Long productColorId, @RequestParam Long sizeId,
            @RequestParam Double price, @RequestParam Integer stock) {
        productService.addVariantToProductColor(productColorId, sizeId, price, stock);
        return ApiResponse.success("ThÃªm size thÃ nh cÃ´ng");
    }

    @PostMapping(value = "/colors/{productColorId}/images", consumes = {"multipart/form-data"})
    public ApiResponse<String> uploadImage(@PathVariable Long productColorId, @RequestParam("imageFiles") MultipartFile[] imageFiles) {
        productService.uploadImages(productColorId, imageFiles);
        return ApiResponse.success("Táº£i áº£nh lÃªn thÃ nh cÃ´ng");
    }

    @DeleteMapping("/variants/sizes/{id}")
    public ApiResponse<String> deleteVariantSize(@PathVariable Long id) {
        productService.deleteVariant(id);
        return ApiResponse.success("XÃ³a size thÃ nh cÃ´ng");
    }

    @DeleteMapping("/variants/images/{id}")
    public ApiResponse<String> deleteImage(@PathVariable Long id) {
        productService.deleteVariantImage(id);
        return ApiResponse.success("XÃ³a áº£nh thÃ nh cÃ´ng");
    }

    @DeleteMapping("/variants/colors/{id}")
    public ApiResponse<String> deleteColorGroup(@PathVariable Long id) {
        productService.deleteProductColor(id);
        return ApiResponse.success("XÃ³a mÃ u sáº¯c khá»\ufffdi sáº£n pháº©m thÃ nh cÃ´ng");
    }

    @PutMapping("/variants/sizes/{id}")
    public ApiResponse<String> updateVariantSize(@PathVariable Long id, @RequestParam Double price,
            @RequestParam Integer stock, @RequestParam VariantStatus status) {
        productService.updateVariant(id, price, stock, status);
        return ApiResponse.success("Cáº­p nháº­t size thÃ nh cÃ´ng");
    }

    @PutMapping("/variants/colors/{id}/toggle-status")
    public ApiResponse<String> toggleColorStatus(@PathVariable Long id) {
        productService.toggleProductColorStatus(id);
        return ApiResponse.success("Cáº­p nháº­t tráº¡ng thÃ¡i mÃ u thÃ nh cÃ´ng");
    }

    @PutMapping("/variants/colors/{id}/set-default")
    public ApiResponse<String> setDefaultColor(@PathVariable Long id, @RequestParam Long productId) {
        productService.setDefaultColor(productId, id);
        return ApiResponse.success("Ä\ufffdáº·t mÃ u máº·c Ä‘á»‹nh thÃ nh cÃ´ng");
    }

    @PutMapping("/variants/images/{id}/type")
    public ApiResponse<String> setImageType(@PathVariable Long id, @RequestParam String type) {
        productService.setImageType(id, ProductImageType.valueOf(type));
        return ApiResponse.success("Ä\ufffdá»•i loáº¡i áº£nh thÃ nh cÃ´ng");
    }
}
