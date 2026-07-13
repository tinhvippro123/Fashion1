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
        return ApiResponse.success(recentlyViewedService.getRecentlyViewedProducts(principal.getName(), 10));
    }

    @PostMapping("/{productId}")
    public ApiResponse<String> addRecentlyViewed(@PathVariable Long productId, Principal principal) {
        recentlyViewedService.addProductToRecentlyViewed(principal.getName(), productId);
        return ApiResponse.success("Ä\ufffdÃ£ lÆ°u vÃ o danh sÃ¡ch vá»«a xem");
    }
}
