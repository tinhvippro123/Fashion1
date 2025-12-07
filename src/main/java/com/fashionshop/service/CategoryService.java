package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.Category;

public interface CategoryService {
	List<Category> getAllCategories();

	Category getCategoryById(Long id);

	void saveCategory(Category category);

	void deleteCategory(Long id);

	List<Category> getAllRootCategories();
}