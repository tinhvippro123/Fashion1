package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController("apiProductController")
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/category/{slug}")
    public ApiResponse<Map<String, Object>> getProductsByCategory(
            @PathVariable("slug") String slug,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) List<String> sizes,
            @RequestParam(name = "color", required = false) List<String> colors,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "sort", required = false, defaultValue = "default") String sort) {

        return ApiResponse.success(productService.getCategoryProductsData(slug, page, sizes, colors, minPrice, maxPrice, sort));
    }

    @GetMapping("/search")
    public ApiResponse<Map<String, Object>> searchProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", required = false) List<String> sizes,
            @RequestParam(name = "color", required = false) List<String> colors,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            @RequestParam(name = "sort", required = false, defaultValue = "default") String sort) {

        return ApiResponse.success(productService.getSearchProductsData(keyword, page, sizes, colors, minPrice, maxPrice, sort));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getProductDetail(
            @PathVariable Long id,
            @RequestParam(name = "color", required = false) String selectedColorName,
            Principal principal) {

        String userEmail = principal != null ? principal.getName() : null;
        return ApiResponse.success(productService.getProductDetailData(id, selectedColorName, userEmail));
    }
}
