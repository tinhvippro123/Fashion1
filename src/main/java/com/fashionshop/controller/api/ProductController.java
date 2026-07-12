package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.model.Category;
import com.fashionshop.model.Product;
import com.fashionshop.service.CategoryService;
import com.fashionshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("apiProductController")
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // 1. Lấy danh sách sản phẩm theo danh mục (hoặc new-arrival)
    @GetMapping("/category/{slug}")
    public ApiResponse<Map<String, Object>> getProductsByCategory(
            @PathVariable("slug") String slug,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) List<String> sizes,
            @RequestParam(name = "color", required = false) List<String> colors,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "sort", required = false, defaultValue = "default") String sort) {

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

        Category category = categoryService.resolveCategoryFromSlug(slug);

        if (category == null) {
            throw new FashionShopException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Page<Product> productPage = productService.searchProductsWithFilters(null, category.getId(), sizeParam, colorParam, minPrice, maxPrice, pageable);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("products", productPage.getContent());
        responseData.put("currentPage", page);
        responseData.put("totalPages", productPage.getTotalPages());
        responseData.put("totalElements", productPage.getTotalElements());

        return ApiResponse.success(responseData);
    }

    // 2. Tìm kiếm sản phẩm
    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) List<String> sizes,
            @RequestParam(name = "color", required = false) List<String> colors,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "sort", required = false, defaultValue = "default") String sort) {

        Sort sortObj = Sort.unsorted();
        if ("price_asc".equals(sort)) {
            sortObj = Sort.by("basePrice").ascending();
        } else if ("price_desc".equals(sort)) {
            sortObj = Sort.by("basePrice").descending();
        } else if ("newest".equals(sort)) {
            sortObj = Sort.by("createdAt").descending();
        }
        Pageable pageable = PageRequest.of(page, 24, sortObj);

        List<String> sizeParam = (sizes != null && !sizes.isEmpty()) ? sizes : null;
        List<String> colorParam = (colors != null && !colors.isEmpty()) ? colors : null;
        Page<Product> productPage = productService.searchProductsWithFilters(keyword, null, sizeParam, colorParam, minPrice, maxPrice, pageable);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("products", productPage.getContent());
        responseData.put("currentPage", page);
        responseData.put("totalPages", productPage.getTotalPages());
        responseData.put("totalElements", productPage.getTotalElements());

        return ApiResponse.success(responseData);
    }

    // 3. Chi tiết sản phẩm
    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getProductDetail(
            @PathVariable Long id,
            @RequestParam(name = "color", required = false) String selectedColorName,
            Principal principal) {

        String userEmail = principal != null ? principal.getName() : null;
        Map<String, Object> responseData = productService.getProductDetailData(id, selectedColorName, userEmail);

        return ApiResponse.success(responseData);
    }
}
