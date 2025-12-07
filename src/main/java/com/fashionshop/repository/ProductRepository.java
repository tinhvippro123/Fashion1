package com.fashionshop.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fashionshop.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// Tìm sản phẩm theo danh mục cha (đệ quy)
	// Query này khá phức tạp vì product nối với category con, ta cần tìm theo
	// category cha
	@Query("SELECT p FROM Product p WHERE p.category.parent.slug = :slug OR p.category.slug = :slug")
	List<Product> findByRootCategorySlug(@Param("slug") String slug);

	// Tìm sản phẩm mới (New Arrival) theo danh mục cha và ngày tạo > ngày truyền
	// vào
	@Query("SELECT p FROM Product p WHERE (p.category.parent.slug = :slug OR p.category.slug = :slug) AND p.createdAt >= :date")
	List<Product> findNewArrivals(@Param("slug") String slug, @Param("date") LocalDateTime date);
}
