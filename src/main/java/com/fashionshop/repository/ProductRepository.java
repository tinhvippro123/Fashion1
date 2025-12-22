package com.fashionshop.repository;

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
//	@Query("SELECT p FROM Product p WHERE (p.category.parent.slug = :slug OR p.category.slug = :slug) AND p.createdAt >= :date")
//	List<Product> findNewArrivals(@Param("slug") String slug, @Param("date") LocalDateTime date);

//	List<Product> findTop10ByOrderByCreatedAtDesc();

	@Query("SELECT p FROM Product p JOIN p.category c WHERE c.name = :categoryName ORDER BY p.createdAt DESC")
	List<Product> findTop10ByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p " +
	           "JOIN p.category c " +
	           "JOIN p.productColors pc " + // Join để check màu
	           "WHERE pc.isActive = true " +  // Chỉ lấy nếu có màu active
	           "AND (c.id = :categoryId OR c.parent.id = :categoryId OR c.parent.parent.id = :categoryId) " +
	           "ORDER BY p.createdAt DESC")
	    List<Product> findTop10ByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

	// HÀM VẠN NĂNG (ĐÃ NÂNG CẤP): Chỉ hiện sản phẩm có ít nhất 1 màu đang Active
	@Query("SELECT DISTINCT p FROM Product p " +
	       "JOIN p.productColors pc " +       // 1. Đổi LEFT JOIN -> JOIN (Bắt buộc SP phải có màu)
	       "LEFT JOIN pc.variants v " +       // Vẫn giữ LEFT JOIN cho variant (phòng khi chưa nhập size)
	       "LEFT JOIN p.category cat " +
	       "WHERE pc.isActive = true " +        // 2. QUAN TRỌNG: Chỉ lấy những dòng có màu đang Active
	       "AND (:keyword IS NULL OR p.name LIKE %:keyword%) " +
	       "AND (:categoryId IS NULL OR cat.id = :categoryId OR cat.parent.id = :categoryId OR cat.parent.parent.id = :categoryId) " +
	       "AND (:sizes IS NULL OR v.size.name IN :sizes) " +
	       "AND (:colors IS NULL OR pc.color.name IN :colors) " +
	       "AND (:minPrice IS NULL OR p.basePrice >= :minPrice) " +
	       "AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice)")
	Page<Product> findProductsWithFilters(
	        @Param("keyword") String keyword,
	        @Param("categoryId") Long categoryId,
	        @Param("sizes") List<String> sizes,
	        @Param("colors") List<String> colors,
	        @Param("minPrice") Double minPrice,
	        @Param("maxPrice") Double maxPrice,
	        Pageable pageable);
	
	// Trong interface ProductRepository
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
	List<Product> searchProducts(@Param("keyword") String keyword);

	// Hoặc nếu bạn muốn phân trang cho kết quả tìm kiếm luôn:
	@Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
	Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
	
	
}
