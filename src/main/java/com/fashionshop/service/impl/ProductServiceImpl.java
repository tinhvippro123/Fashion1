package com.fashionshop.service.impl;

import com.fashionshop.model.Color;
import com.fashionshop.model.Product;
import com.fashionshop.model.ProductColor;
import com.fashionshop.model.Size;
import com.fashionshop.model.Variant;
import com.fashionshop.model.VariantImage;
import com.fashionshop.model.Category;
import com.fashionshop.repository.ColorRepository;
import com.fashionshop.repository.ProductColorRepository;
import com.fashionshop.repository.ProductRepository;
import com.fashionshop.repository.SizeRepository;
import com.fashionshop.repository.VariantImageRepository;
import com.fashionshop.repository.VariantRepository;
import com.fashionshop.repository.CategoryRepository;
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
	private CategoryRepository categoryRepository;

	@Autowired
	private StorageService storageService;

	@Override
	public Page<Product> getAllProducts(Pageable pageable) {
		return productRepository.findAll(pageable);
	}

	@Override
	public Page<Product> searchAdminProducts(String keyword, Pageable pageable) {
		if (keyword == null || keyword.trim().isEmpty()) {
			return productRepository.findAll(pageable);
		}
		return productRepository.searchProducts(keyword.trim(), pageable);
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
			if (pc.getVariants() != null) {
				boolean exists = pc.getVariants().stream().anyMatch(v -> v.getSize().getId().equals(sizeId));
				if (exists) return; // Không thêm trùng Size
			}

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
			
			if (pc.getVariants() != null) {
				pc.getVariants().add(variant);
			}
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
			int currentSize = (currentImages == null) ? 0 : currentImages.size();

			if (currentSize == 0) {
				img.setImageType(ProductImageType.MAIN);
			} else if (currentSize == 1) {
				img.setImageType(ProductImageType.HOVER);
			} else {
				img.setImageType(ProductImageType.EXTRA);
			}
			img.setSortOrder(currentSize + 1);

			variantImageRepository.save(img);
			
			if (currentImages != null) {
				currentImages.add(img);
			}
		}
	}

	@Override
	public void setImageType(Long imageId, ProductImageType type) {
		VariantImage targetImg = variantImageRepository.findById(imageId).orElse(null);
		if (targetImg != null) {
			ProductColor pc = targetImg.getProductColor();
			// Nếu muốn set làm MAIN hoặc HOVER, cần tìm ảnh nào đang giữ vai trò này và hạ cấp nó xuống EXTRA
			if (type == ProductImageType.MAIN || type == ProductImageType.HOVER) {
				for (VariantImage img : pc.getImages()) {
					if (img.getImageType() == type) {
						img.setImageType(ProductImageType.EXTRA);
						variantImageRepository.save(img);
					}
				}
			}
			targetImg.setImageType(type);
			variantImageRepository.save(targetImg);
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
        Category categoryNu = categoryRepository.findBySlug("nu");
        if (categoryNu == null) return new java.util.ArrayList<>();
        return productRepository.findTop10ByCategoryId(categoryNu.getId(), PageRequest.of(0, 10));
    }

    @Override
    public List<Product> findTop10NewestMen() {
        Category categoryNam = categoryRepository.findBySlug("nam");
        if (categoryNam == null) return new java.util.ArrayList<>();
        return productRepository.findTop10ByCategoryId(categoryNam.getId(), PageRequest.of(0, 10));
    }

    @Override
    public Page<Product> searchProductsWithFilters(String keyword, Long categoryId, List<String> sizes, List<String> colors, Double minPrice, Double maxPrice, Pageable pageable) {
        keyword = (keyword == null) ? "" : keyword;
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