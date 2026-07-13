package com.fashionshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fashionshop.enums.ProductImageType;
import com.fashionshop.model.Product;
import com.fashionshop.dto.admin.ProductRequestDTO;
import com.fashionshop.dto.admin.ProductRequestDTO;
import com.fashionshop.model.Variant;
import com.fashionshop.enums.VariantStatus;

public interface ProductService {
	Page<Product> getAllProducts(Pageable pageable);
	Page<Product> searchAdminProducts(String keyword, Pageable pageable);
	Product getProductById(Long id);

	Product saveProduct(Product product);
	Product createProduct(ProductRequestDTO request);
	Product updateProduct(Long id, ProductRequestDTO request);

	void deleteProduct(Long id);

	void addColorToProduct(Long productId, Long colorId);

	void addVariantToProductColor(Long productColorId, Long sizeId, Double price, Integer stock);

	void addImageToProductColor(Long productColorId, String imageUrl);

	void setImageType(Long imageId, ProductImageType type);

	void deleteVariant(Long variantId);

	void deleteVariantImage(Long imageId);

	void deleteProductColor(Long productColorId);

	Variant getVariantById(Long id);

	void updateVariant(Long variantId, Double newPrice, Integer newStock, VariantStatus newStatus);

	void toggleProductColorStatus(Long productColorId);

	List<Product> getProductsByCategorySlug(String slug);

	List<Product> findTop10NewestWomen(); // Lấy đồ Nữ

	List<Product> findTop10NewestMen(); // Lấy đồ Nam

	Page<Product> searchProductsWithFilters(String keyword, Long categoryId, List<String> sizes, List<String> colors,
			Double minPrice, Double maxPrice, Pageable pageable);

	void setDefaultColor(Long productId, Long colorId);

	Product getProductWithActiveColors(Long id);

	java.util.Map<String, Object> getProductDetailData(Long id, String selectedColorName, String userEmail);

	long countAllProducts();
    void uploadImages(Long productColorId, org.springframework.web.multipart.MultipartFile[] imageFiles);
    
    java.util.Map<String, Object> getSearchSuggestions(String keyword);

	java.util.Map<String, Object> getCategoryProductsData(String slug, int page, java.util.List<String> sizes, java.util.List<String> colors, Double minPrice, Double maxPrice, String sort);
	java.util.Map<String, Object> getSearchProductsData(String keyword, int page, java.util.List<String> sizes, java.util.List<String> colors, Double minPrice, Double maxPrice, String sort);
	
	java.util.Map<String, Object> getHomeData(java.util.List<com.fashionshop.model.Banner> activeBanners);
}