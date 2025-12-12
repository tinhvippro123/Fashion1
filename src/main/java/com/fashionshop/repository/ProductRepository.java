package com.fashionshop.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

	List<Product> findTop10ByOrderByCreatedAtDesc();

	@Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName ORDER BY p.createdAt DESC")
	List<Product> findTop10ByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p " + "JOIN p.category c " + "WHERE c.id = :categoryId "
			+ "OR c.parent.id = :categoryId " + "OR c.parent.parent.id = :categoryId " + "ORDER BY p.createdAt DESC")
	List<Product> findTop10ByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p " +
	           "JOIN p.category cat " +               // 1. Join Danh mục
	           "LEFT JOIN p.productColors pc " +        // 2. Join sang Màu (ProductColor)
	           "LEFT JOIN pc.variants v " +     // 3. TỪ MÀU MỚI JOIN SANG BIẾN THỂ (Variant)
	           "LEFT JOIN v.size s " +                 // 4. Từ Biến thể lấy Size
	           "LEFT JOIN pc.color c " +                // 5. Từ ProductColor lấy tên Màu (Đen/Trắng)
	           "WHERE (cat.id = :categoryId OR cat.parent.id = :categoryId OR cat.parent.parent.id = :categoryId) " +
	           "AND (:sizes IS NULL OR s.name IN :sizes) " +     // Lọc Size
	           "AND (:colors IS NULL OR c.name IN :colors) " +   // Lọc Màu
	           "AND (:minPrice IS NULL OR p.basePrice >= :minPrice) " +
	           "AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice) " +
	           "ORDER BY p.createdAt DESC")
	    Page<Product> findWithFilters(@Param("categoryId") Long categoryId,
	                                  @Param("sizes") List<String> sizes,
	                                  @Param("colors") List<String> colors,
	                                  @Param("minPrice") Double minPrice,
	                                  @Param("maxPrice") Double maxPrice,
	                                  Pageable pageable);

}
