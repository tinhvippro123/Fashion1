package com.fashionshop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fashionshop.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	List<Category> findByParentIsNull();

	Category findBySlug(String slug);

}
