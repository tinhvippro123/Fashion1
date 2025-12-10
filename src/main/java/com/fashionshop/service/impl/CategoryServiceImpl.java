package com.fashionshop.service.impl;

import com.fashionshop.model.Category;
import com.fashionshop.repository.CategoryRepository;
import com.fashionshop.service.CategoryService;
import com.fashionshop.utils.SlugUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public Category getCategoryById(Long id) {
		return categoryRepository.findById(id).orElse(null);
	}

	@Override
	public void saveCategory(Category category) {
		// Logic: Tạo slug từ tên (Đơn giản hóa, thực tế nên dùng thư viện slugify)
		if (category.getSlug() == null || category.getSlug().isEmpty()) {
			category.setSlug(SlugUtil.makeSlug(category.getName()));
		}

		// Logic: Set thời gian
		if (category.getId() == null) {
			category.setCreatedAt(LocalDateTime.now());
		}
		category.setUpdatedAt(LocalDateTime.now());

		// Logic: Mặc định active nếu null
		if (category.getIsActive() == null)
			category.setIsActive(true);

		categoryRepository.save(category);
	}

	@Override
	public void deleteCategory(Long id) {
		// Logic: Có thể kiểm tra xem có sản phẩm nào thuộc danh mục này không trước khi
		// xóa
		categoryRepository.deleteById(id);
	}

	@Override
	public List<Category> getAllRootCategories() {
		return categoryRepository.findByParentIsNull();
	}

	@Override
	public Category findBySlug(String slug) {
		return categoryRepository.findBySlug(slug);
	}

}