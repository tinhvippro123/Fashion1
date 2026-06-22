package com.fashionshop.controller.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.util.Map;

@RestController
public class DebugController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/debug/products")
    public List<Map<String, Object>> getProducts() {
        return jdbcTemplate.queryForList("SELECT id, name, category_id, is_active FROM products");
    }

    @GetMapping("/debug/categories")
    public List<Map<String, Object>> getCategories() {
        return jdbcTemplate.queryForList("SELECT id, name, slug, parent_id FROM categories");
    }

    @GetMapping("/debug/colors")
    public List<Map<String, Object>> getColors() {
        return jdbcTemplate.queryForList("SELECT id, product_id, color_id, is_active FROM product_colors");
    }
}
