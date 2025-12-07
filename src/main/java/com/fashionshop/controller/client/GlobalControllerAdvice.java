package com.fashionshop.controller.client;

import com.fashionshop.model.Category;
import com.fashionshop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice(basePackages = "com.fashionshop.controller.client") // Chỉ áp dụng cho các controller của Client
public class GlobalControllerAdvice {

    @Autowired
    private CategoryService categoryService;

    @ModelAttribute("categories") // Tên biến dùng trong HTML sẽ là ${categories}
    public List<Category> populateCategories() {
        // Logic: Chỉ lấy các danh mục GỐC (Parent = null)
        // Vì trong Entity Category mình đã map @OneToMany children, 
        // nên từ cha sẽ tự lấy được con.
        return categoryService.getAllRootCategories(); 
    }
}