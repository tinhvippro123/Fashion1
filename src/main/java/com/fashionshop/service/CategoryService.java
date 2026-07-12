package com.fashionshop.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fashionshop.model.Category;

public interface CategoryService {
	List<Category> getAllCategories();
	Page<Category> getAllCategories(Pageable pageable);
	Page<Category> searchCategories(String keyword, Pageable pageable);

	Category getCategoryById(Long id);

	void saveCategory(Category category);

	void deleteCategory(Long id);

	List<Category> getAllRootCategories();

	Category findBySlug(String slug);
	Category resolveCategoryFromSlug(String slug);
}