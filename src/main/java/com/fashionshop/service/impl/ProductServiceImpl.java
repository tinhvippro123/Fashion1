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
import com.fashionshop.utils.SlugUtil;
import com.fashionshop.enums.ProductImageType;
import com.fashionshop.enums.VariantStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private ProductColorRepository productColorRepository;
	@Autowired
	private VariantRepository variantRepository;
	@Autowired
	private VariantImageRepository variantImageRepository;
	@Autowired
	private ColorRepository colorRepository;
	@Autowired
	private SizeRepository sizeRepository;

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
			product.setSlug(SlugUtil.makeSlug(product.getName()));
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
				pc.setIsActive(true);
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

			if (stock <= 0) {
				variant.setStatus(VariantStatus.OUT_OF_STOCK);
			} else {
				variant.setStatus(VariantStatus.AVAILABLE);
			}

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


//			Lấy danh sách ảnh hiện tại để đếm
			List<VariantImage> currentImages = pc.getImages();

//			Tự động set thứ tự (Sort Order)
			img.setSortOrder(currentImages.size() + 1);

//			Tự động set Loại ảnh (MAIN / HOVER / EXTRA)
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
//			Xóa file vật lý trên ổ cứng
			storageService.delete(image.getImageUrl());
//			Xóa dữ liệu trong DB
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


			if (newStock <= 0) {
//				 1. Nếu tồn kho về 0 -> Bắt buộc chuyển thành HẾT HÀNG
				variant.setStatus(VariantStatus.OUT_OF_STOCK);
			} else {
//				 2. Nếu có hàng (Stock > 0)

//				 Trường hợp Admin đang chọn nhầm "Hết hàng" trong dropdown -> Tự sửa thành
//				 "Đang bán"
				if (newStatus == VariantStatus.OUT_OF_STOCK) {
					variant.setStatus(VariantStatus.AVAILABLE);
				} else {
//					 Còn lại thì tôn trọng lựa chọn của Admin (Có thể là AVAILABLE hoặc HIDDEN)
					variant.setStatus(newStatus);
				}
			}

			variantRepository.save(variant);
		}
	}

	@Override
	public void toggleProductColorStatus(Long productColorId) {
		ProductColor pc = productColorRepository.findById(productColorId).orElse(null);
		if (pc != null) {
//			 Đảo ngược trạng thái: True thành False, False thành True
//			 Nếu null thì coi như là false -> set thành true
			boolean currentStatus = pc.getIsActive() == null ? false : pc.getIsActive();
			pc.setIsActive(!currentStatus);

			productColorRepository.save(pc);
		}
	}

	@Override
	public List<Product> getProductsByCategorySlug(String slug) {
	    return productRepository.findByRootCategorySlug(slug);
	}	
	
	@Override
    public List<Product> findTop10NewestWomen() {
        // THAY SỐ 1 BẰNG ID DANH MỤC "NỮ" TRONG DB CỦA BẠN
        Long categoryIdNu = 2L; 
        return productRepository.findTop10ByCategoryId(categoryIdNu, PageRequest.of(0, 10));
    }

    @Override
    public List<Product> findTop10NewestMen() {
        // THAY SỐ 2 BẰNG ID DANH MỤC "NAM" TRONG DB CỦA BẠN
        Long categoryIdNam = 1L;
        return productRepository.findTop10ByCategoryId(categoryIdNam, PageRequest.of(0, 10));
    }

    @Override
    public Page<Product> searchProductsWithFilters(String keyword, Long categoryId, List<String> sizes, List<String> colors, Double minPrice, Double maxPrice, Pageable pageable) {
        return productRepository.findProductsWithFilters(keyword, categoryId, sizes, colors, minPrice, maxPrice, pageable);
    }

    
    public void setDefaultColor(Long productId, Long colorId) {
        Product product = getProductById(productId);
        if (product != null && product.getProductColors() != null) {
            for (ProductColor pc : product.getProductColors()) {
                if (pc.getId().equals(colorId)) {
                    pc.setIsDefault(true);
                    pc.setIsActive(true);
                } else {
                    pc.setIsDefault(false);
                }
            }
            saveProduct(product);
        }
    }
    
    @Override
    public Product getProductWithActiveColors(Long id) {
        // 1. Lấy sản phẩm gốc
        Product product = productRepository.findById(id).orElse(null);

        // 2. Kiểm tra null hoặc bị ẩn
        if (product == null || !product.getIsActive()) {
            return null;
        }

        // 3. Logic lọc màu (Chuyển từ Controller sang đây)
        List<ProductColor> activeColors = product.getProductColors().stream()
                .filter(ProductColor::getIsActive)
                .collect(Collectors.toList());

        // Nếu không còn màu nào -> Coi như null
        if (activeColors.isEmpty()) {
            return null;
        }

        // Gán lại list màu đã lọc
        product.setProductColors(activeColors);
        
        return product;
    }
    
    public long countAllProducts() {
        return productRepository.count();
    }
}