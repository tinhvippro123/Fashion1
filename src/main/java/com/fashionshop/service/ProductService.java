package com.fashionshop.service;

import java.util.List;

import com.fashionshop.model.Product;
import com.fashionshop.model.Variant;
import com.fashionshope.enums.VariantStatus;

public interface ProductService {
	List<Product> getAllProducts();

	Product getProductById(Long id);

	Product saveProduct(Product product);

	void deleteProduct(Long id);

	void addColorToProduct(Long productId, Long colorId);

	void addVariantToProductColor(Long productColorId, Long sizeId, Double price, Integer stock);

	void addImageToProductColor(Long productColorId, String imageUrl);

	void deleteVariant(Long variantId);

	void deleteVariantImage(Long imageId);

	void deleteProductColor(Long productColorId);

	Variant getVariantById(Long id);

	void updateVariant(Long variantId, Double newPrice, Integer newStock, VariantStatus newStatus);
}