package com.fashionshop.service.impl;

import com.fashionshop.model.Color;
import com.fashionshop.model.Product;
import com.fashionshop.model.ProductColor;
import com.fashionshop.model.Size;
import com.fashionshop.model.Variant;
import com.fashionshop.model.VariantImage;
import com.fashionshop.repository.ColorRepository;
import com.fashionshop.repository.ProductColorRepository;
import com.fashionshop.repository.ProductRepository;
import com.fashionshop.repository.SizeRepository;
import com.fashionshop.repository.VariantImageRepository;
import com.fashionshop.repository.VariantRepository;
import com.fashionshop.service.ProductService;
import com.fashionshop.service.StorageService;
import com.fashionshope.enums.ProductImageType;
import com.fashionshope.enums.VariantStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductColorRepository productColorRepository; // Mới
	@Autowired
	private VariantRepository variantRepository; // Mới
	@Autowired
	private VariantImageRepository variantImageRepository; // Mới
	@Autowired
	private ColorRepository colorRepository; // Mới
	@Autowired
	private SizeRepository sizeRepository; // Mới

	@Autowired
	private StorageService storageService;

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product getProductById(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public Product saveProduct(Product product) {
		// Logic tạo slug (URL thân thiện) từ tên sản phẩm
		if (product.getSlug() == null || product.getSlug().isEmpty()) {
			product.setSlug(product.getName().toLowerCase().replace(" ", "-"));
		}

		if (product.getId() == null) {
			product.setCreatedAt(LocalDateTime.now());
		}
		product.setUpdatedAt(LocalDateTime.now());

		return productRepository.save(product);
	}

	@Override
	public void deleteProduct(Long id) {
		productRepository.deleteById(id);
	}

	@Override
	public void addColorToProduct(Long productId, Long colorId) {
		Product product = getProductById(productId);
		Color color = colorRepository.findById(colorId).orElse(null);

		if (product != null && color != null) {
			// Kiểm tra xem màu này đã có trong sản phẩm chưa để tránh trùng
			boolean exists = product.getProductColors().stream().anyMatch(pc -> pc.getColor().getId().equals(colorId));

			if (!exists) {
				ProductColor pc = new ProductColor();
				pc.setProduct(product);
				pc.setColor(color);
				pc.setIsDefault(false); // Mặc định là false, logic set true tính sau
				productColorRepository.save(pc);
			}
		}
	}

	@Override
	public void addVariantToProductColor(Long productColorId, Long sizeId, Double price, Integer stock) {
		ProductColor pc = productColorRepository.findById(productColorId).orElse(null);
		Size size = sizeRepository.findById(sizeId).orElse(null);

		if (pc != null && size != null) {
			Variant variant = new Variant();
			variant.setProductColor(pc);
			variant.setSize(size);
			variant.setPrice(price);
			variant.setStock(stock);
			variant.setStatus(VariantStatus.AVAILABLE); // Mặc định
			variantRepository.save(variant);
		}
	}

	@Override
	public void addImageToProductColor(Long productColorId, String imageUrl) {
		ProductColor pc = productColorRepository.findById(productColorId).orElse(null);
		if (pc != null) {
			VariantImage img = new VariantImage();
			img.setProductColor(pc);
			img.setImageUrl(imageUrl);

			// --- LOGIC MỚI CẬP NHẬT ---

			// 1. Lấy danh sách ảnh hiện tại để đếm
			List<VariantImage> currentImages = pc.getImages();

			// 2. Tự động set thứ tự (Sort Order)
			img.setSortOrder(currentImages.size() + 1);

			// 3. Tự động set Loại ảnh (MAIN / HOVER / EXTRA)
			if (currentImages.isEmpty()) {
				img.setImageType(ProductImageType.MAIN); // Ảnh đầu tiên là MAIN
			} else if (currentImages.size() == 1) {
				img.setImageType(ProductImageType.HOVER); // Ảnh thứ 2 là HOVER
			} else {
				img.setImageType(ProductImageType.EXTRA); // Còn lại là ảnh phụ
			}

			variantImageRepository.save(img);
		}
	}

	@Override
	public void deleteVariant(Long variantId) {
		variantRepository.deleteById(variantId);
	}

	@Override
	public void deleteVariantImage(Long imageId) {
		VariantImage image = variantImageRepository.findById(imageId).orElse(null);
		if (image != null) {
			// 1. Xóa file vật lý trên ổ cứng
			storageService.delete(image.getImageUrl());
			// 2. Xóa dữ liệu trong DB
			variantImageRepository.delete(image);
		}
	}

	@Override
	public void deleteProductColor(Long productColorId) {
		// Khi xóa ProductColor, nhờ CascadeType.ALL trong Entity,
		// nó sẽ tự động xóa hết Variants và Images con của nó.
		// Tuy nhiên, muốn sạch sẽ file ảnh, bạn nên loop xóa ảnh trước (tùy chọn).
		// Ở đây làm đơn giản:
		productColorRepository.deleteById(productColorId);
	}

	@Override
	public Variant getVariantById(Long id) {
		return variantRepository.findById(id).orElse(null);
	}

	@Override
	public void updateVariant(Long variantId, Double newPrice, Integer newStock, VariantStatus newStatus) {
		Variant variant = variantRepository.findById(variantId).orElse(null);
		if (variant != null) {
			variant.setPrice(newPrice);
			variant.setStock(newStock);
			variant.setStatus(newStatus);
			// Có thể thêm logic cập nhật status nếu stock = 0
			variantRepository.save(variant);
		}
	}

}