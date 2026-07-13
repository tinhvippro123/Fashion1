package com.fashionshop.service.impl;

import com.fashionshop.dto.admin.CategoryRequestDTO;

import com.fashionshop.exception.ErrorCode;

import com.fashionshop.exception.FashionShopException;



import com.fashionshop.model.Category;

import com.fashionshop.repository.CategoryRepository;

import com.fashionshop.service.CategoryService;

import com.fashionshop.utils.SlugUtil;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;



@Service

public class CategoryServiceImpl implements CategoryService {



	@Autowired

	private CategoryRepository categoryRepository;



	@Override

	public List<Category> getAllCategories() {

		return categoryRepository.findAll();

	}



	@Override

	public Page<Category> getAllCategories(Pageable pageable) {

		return categoryRepository.findAll(pageable);

	}



	@Override

	public Page<Category> searchCategories(String keyword, Pageable pageable) {

		if (keyword == null || keyword.trim().isEmpty()) {

			return categoryRepository.findAll(pageable);

		}

		return categoryRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);

	}



	@Override

	public Category getCategoryById(Long id) {

		return categoryRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.CATEGORY_NOT_FOUND));

	}



	@Override

	public Category saveCategory(Category category) {

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



		return categoryRepository.save(category);

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



	@Override

	public Category resolveCategoryFromSlug(String slug) {

		if (slug.equals("hang-nam-moi-ve")) {

			return categoryRepository.findBySlug("nam");

		} else if (slug.equals("hang-nu-moi-ve")) {

			return categoryRepository.findBySlug("nu");

		} else {

			return categoryRepository.findBySlug(slug);

		}

	}



    @Override

    public Category createCategory(CategoryRequestDTO request) {

        Category category = new Category();

        category.setName(request.getName());

        return saveCategory(category);

    }



    @Override

    public Category updateCategory(Long id, CategoryRequestDTO request) {

        Category category = getCategoryById(id);

        category.setName(request.getName());

        return saveCategory(category);

    }





}

