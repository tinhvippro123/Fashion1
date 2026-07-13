package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.Category;
import com.fashionshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        // Tráº£ vá»\ufffd danh sÃ¡ch táº¥t cáº£ cÃ¡c danh má»¥c Ä‘á»ƒ xÃ¢y dá»±ng Menu Header
        List<Category> categories = categoryService.getAllCategories();
        return ApiResponse.success(categories);
    }
}
