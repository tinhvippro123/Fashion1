package com.fashionshop.controller.api;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.exception.ErrorCode;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.Product;
import com.fashionshop.model.User;
import com.fashionshop.service.ProductService;
import com.fashionshop.service.RecentlyViewedService;
import com.fashionshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recently-viewed")
public class RecentlyViewedController {

    @Autowired
    private RecentlyViewedService recentlyViewedService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping
    public ApiResponse<List<Product>> getRecentlyViewed(Principal principal) {
        User user = userService.getUserByEmailOrThrow(principal.getName());
        List<Product> products = recentlyViewedService.getRecentlyViewedProducts(user, 10);
        return ApiResponse.success(products);
    }

    @PostMapping("/{productId}")
    public ApiResponse<String> addRecentlyViewed(@PathVariable Long productId, Principal principal) {
        User user = userService.getUserByEmailOrThrow(principal.getName());
        Product product = productService.getProductById(productId);
        
        if (product == null) {
            throw new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND, "Sản phẩm không tồn tại");
        }
        
        recentlyViewedService.addProductToRecentlyViewed(user, product);
        return ApiResponse.success("Đã lưu vào danh sách vừa xem");
    }
}
