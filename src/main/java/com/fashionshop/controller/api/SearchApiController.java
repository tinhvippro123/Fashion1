package com.fashionshop.controller.api;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.service.ProductService;

@RestController
@RequestMapping("/api/v1/search")
public class SearchApiController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ApiResponse<Map<String, Object>> liveSearch(@RequestParam("keyword") String keyword) {
        return ApiResponse.success(productService.getSearchSuggestions(keyword));
    }
}
