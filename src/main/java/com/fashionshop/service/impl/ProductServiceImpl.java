package com.fashionshop.service.impl;
import com.fashionshop.dto.admin.ProductRequestDTO;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;

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
import com.fashionshop.service.UserService;
import com.fashionshop.service.RecentlyViewedService;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.model.User;
import com.fashionshop.utils.SlugUtil;
import com.fashionshop.enums.ProductImageType;
import com.fashionshop.enums.VariantStatus;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private com.fashionshop.service.CategoryService categoryService;

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
	private UserService userService;
	@Autowired
	private RecentlyViewedService recentlyViewedService;

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
		return productRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	@Override
	public Product saveProduct(Product product) {
		// Logic táº¡o slug (URL thÃ¢n thiá»‡n) tá»« tÃªn sáº£n pháº©m
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
		Color color = colorRepository.findById(colorId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));

		if (product != null && color != null) {
			// Kiá»ƒm tra xem mÃ u nÃ y Ä‘Ã£ cÃ³ trong sáº£n pháº©m chÆ°a Ä‘á»ƒ trÃ¡nh trÃ¹ng
			boolean exists = product.getProductColors().stream().anyMatch(pc -> pc.getColor().getId().equals(colorId));

			if (!exists) {
				ProductColor pc = new ProductColor();
				pc.setProduct(product);
				pc.setColor(color);
				pc.setIsDefault(false); // Máº·c Ä‘á»‹nh lÃ  false, logic set true tÃ­nh sau
				pc.setIsActive(true);
				productColorRepository.save(pc);
			}
		}
	}

	@Override
	public void addVariantToProductColor(Long productColorId, Long sizeId, Double price, Integer stock) {
		ProductColor pc = productColorRepository.findById(productColorId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
		Size size = sizeRepository.findById(sizeId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));

		if (pc != null && size != null) {
			if (pc.getVariants() != null) {
				boolean exists = pc.getVariants().stream().anyMatch(v -> v.getSize().getId().equals(sizeId));
				if (exists) return; // KhÃ´ng thÃªm trÃ¹ng Size
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
		ProductColor pc = productColorRepository.findById(productColorId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
		if (pc != null) {
			VariantImage img = new VariantImage();
			img.setProductColor(pc);
			img.setImageUrl(imageUrl);

//			Láº¥y danh sÃ¡ch áº£nh hiá»‡n táº¡i Ä‘á»ƒ Ä‘áº¿m
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
		VariantImage targetImg = variantImageRepository.findById(imageId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
		if (targetImg != null) {
			ProductColor pc = targetImg.getProductColor();
			// Náº¿u muá»‘n set lÃ m MAIN hoáº·c HOVER, cáº§n tÃ¬m áº£nh nÃ o Ä‘ang giá»¯ vai trÃ² nÃ y vÃ  háº¡ cáº¥p nÃ³ xuá»‘ng EXTRA
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
		VariantImage image = variantImageRepository.findById(imageId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
		if (image != null) {
//			XÃ³a file váº­t lÃ½ trÃªn á»• cá»©ng
			storageService.delete(image.getImageUrl());
//			XÃ³a dá»¯ liá»‡u trong DB
			variantImageRepository.delete(image);
		}
	}

	@Override
	public void deleteProductColor(Long productColorId) {
		// Khi xÃ³a ProductColor, nhá»\ufffd CascadeType.ALL trong Entity,
		// nÃ³ sáº½ tá»± Ä‘á»™ng xÃ³a háº¿t Variants vÃ  Images con cá»§a nÃ³.
		// Tuy nhiÃªn, muá»‘n sáº¡ch sáº½ file áº£nh, báº¡n nÃªn loop xÃ³a áº£nh trÆ°á»›c (tÃ¹y chá»\ufffdn).
		// á»ž Ä‘Ã¢y lÃ m Ä‘Æ¡n giáº£n:
		productColorRepository.deleteById(productColorId);
	}

	@Override
	public Variant getVariantById(Long id) {
		return variantRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
	}

	@Override
	public void updateVariant(Long variantId, Double newPrice, Integer newStock, VariantStatus newStatus) {
		Variant variant = variantRepository.findById(variantId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
		if (variant != null) {
			variant.setPrice(newPrice);
			variant.setStock(newStock);


			if (newStock <= 0) {
//				 1. Náº¿u tá»“n kho vá»\ufffd 0 -> Báº¯t buá»™c chuyá»ƒn thÃ nh Háº¾T HÃ€NG
				variant.setStatus(VariantStatus.OUT_OF_STOCK);
			} else {
//				 2. Náº¿u cÃ³ hÃ ng (Stock > 0)

//				 TrÆ°á»\ufffdng há»£p Admin Ä‘ang chá»\ufffdn nháº§m "Háº¿t hÃ ng" trong dropdown -> Tá»± sá»­a thÃ nh
//				 "Ä\ufffdang bÃ¡n"
				if (newStatus == VariantStatus.OUT_OF_STOCK) {
					variant.setStatus(VariantStatus.AVAILABLE);
				} else {
//					 CÃ²n láº¡i thÃ¬ tÃ´n trá»\ufffdng lá»±a chá»\ufffdn cá»§a Admin (CÃ³ thá»ƒ lÃ  AVAILABLE hoáº·c HIDDEN)
					variant.setStatus(newStatus);
				}
			}

			variantRepository.save(variant);
		}
	}

	@Override
	public void toggleProductColorStatus(Long productColorId) {
		ProductColor pc = productColorRepository.findById(productColorId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
		if (pc != null) {
//			 Ä\ufffdáº£o ngÆ°á»£c tráº¡ng thÃ¡i: True thÃ nh False, False thÃ nh True
//			 Náº¿u null thÃ¬ coi nhÆ° lÃ  false -> set thÃ nh true
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
        // 1. Láº¥y sáº£n pháº©m gá»‘c
        Product product = productRepository.findById(id).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));

        // 2. Kiá»ƒm tra null hoáº·c bá»‹ áº©n
        if (product == null || !product.getIsActive()) {
            return null;
        }

        // 3. Logic lá»\ufffdc mÃ u (Chuyá»ƒn tá»« Controller sang Ä‘Ã¢y)
        List<ProductColor> activeColors = product.getProductColors().stream()
                .filter(ProductColor::getIsActive)
                .collect(Collectors.toList());

        // Náº¿u khÃ´ng cÃ²n mÃ u nÃ o -> Coi nhÆ° null
        if (activeColors.isEmpty()) {
            return null;
        }

        // GÃ¡n láº¡i list mÃ u Ä‘Ã£ lá»\ufffdc
        product.setProductColors(activeColors);
        
        return product;
    }
    
    public long countAllProducts() {
        return productRepository.count();
    }
    
    @Override
    public Map<String, Object> getProductDetailData(Long id, String selectedColorName, String userEmail) {
        Product product = getProductWithActiveColors(id);
        
        if (product == null) {
            throw new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        if (userEmail != null) {
            User user = userService.findByEmail(userEmail);
            if (user != null) {
                recentlyViewedService.addProductToRecentlyViewed(user, product);
            }
        }

        ProductColor selectedColor = product.getProductColors().isEmpty() ? null : product.getProductColors().get(0);

        if (selectedColorName != null && !selectedColorName.isEmpty() && product.getProductColors() != null) {
            for (ProductColor pc : product.getProductColors()) {
                if (pc.getColor().getName().equalsIgnoreCase(selectedColorName)) {
                    selectedColor = pc;
                    break;
                }
            }
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("product", product);
        responseData.put("selectedColor", selectedColor);

        return responseData;
    }

    @Override
    public Product createProduct(ProductRequestDTO request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        
        com.fashionshop.model.Category cat = new com.fashionshop.model.Category();
        cat.setId(request.getCategoryId());
        product.setCategory(cat);
        
        return saveProduct(product);
    }

    @Override
    public Product updateProduct(Long id, ProductRequestDTO request) {
        Product product = getProductById(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBasePrice(request.getBasePrice());
        
        com.fashionshop.model.Category cat = new com.fashionshop.model.Category();
        cat.setId(request.getCategoryId());
        product.setCategory(cat);
        
        return saveProduct(product);
    }

    @Override
    public void uploadImages(Long productColorId, org.springframework.web.multipart.MultipartFile[] imageFiles) {
        java.util.List<java.util.concurrent.CompletableFuture<String>> futures = new java.util.ArrayList<>();
        for (org.springframework.web.multipart.MultipartFile file : imageFiles) {
            if (!file.isEmpty()) {
                futures.add(java.util.concurrent.CompletableFuture.supplyAsync(() -> storageService.store(file)));
            }
        }
        java.util.List<String> fileNames = futures.stream().map(java.util.concurrent.CompletableFuture::join).collect(java.util.stream.Collectors.toList());
        for (String fileName : fileNames) {
            addImageToProductColor(productColorId, fileName);
        }
    }

    @Override
    public Map<String, Object> getSearchSuggestions(String keyword) {
        Page<Product> productPage = searchProductsWithFilters(
                keyword, null, null, null, null, null, PageRequest.of(0, 4));

        List<com.fashionshop.dto.ProductSuggestDTO> suggestions = productPage.getContent().stream().map(p -> {
            String imageUrl = "";
            if (p.getProductColors() != null && !p.getProductColors().isEmpty() 
                    && p.getProductColors().get(0).getImages() != null 
                    && !p.getProductColors().get(0).getImages().isEmpty()) {
                imageUrl = p.getProductColors().get(0).getImages().get(0).getImageUrl();
            }
            return new com.fashionshop.dto.ProductSuggestDTO(p.getId(), p.getName(), p.getSlug(), p.getBasePrice(), imageUrl);
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("suggestions", suggestions);
        response.put("totalElements", productPage.getTotalElements());

        return response;
    }

    @Override
    public Map<String, Object> getCategoryProductsData(String slug, int page, java.util.List<String> sizes, java.util.List<String> colors, Double minPrice, Double maxPrice, String sort) {
        Sort sortObj = Sort.unsorted();
        if ("price_asc".equals(sort)) {
            sortObj = Sort.by("basePrice").ascending();
        } else if ("price_desc".equals(sort)) {
            sortObj = Sort.by("basePrice").descending();
        } else if ("newest".equals(sort)) {
            sortObj = Sort.by("createdAt").descending();
        }
        Pageable pageable = PageRequest.of(page, 24, sortObj);

        java.util.List<String> sizeParam = (sizes != null && !sizes.isEmpty()) ? sizes : null;
        java.util.List<String> colorParam = (colors != null && !colors.isEmpty()) ? colors : null;

        Category category = categoryService.resolveCategoryFromSlug(slug);
        if (category == null) {
            throw new FashionShopException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Page<Product> productPage = searchProductsWithFilters(null, category.getId(), sizeParam, colorParam, minPrice, maxPrice, pageable);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("products", productPage.getContent());
        responseData.put("currentPage", page);
        responseData.put("totalPages", productPage.getTotalPages());
        responseData.put("totalElements", productPage.getTotalElements());

        return responseData;
    }

    @Override
    public Map<String, Object> getSearchProductsData(String keyword, int page, java.util.List<String> sizes, java.util.List<String> colors, Double minPrice, Double maxPrice, String sort) {
        Sort sortObj = Sort.unsorted();
        if ("price_asc".equals(sort)) {
            sortObj = Sort.by("basePrice").ascending();
        } else if ("price_desc".equals(sort)) {
            sortObj = Sort.by("basePrice").descending();
        } else if ("newest".equals(sort)) {
            sortObj = Sort.by("createdAt").descending();
        }
        Pageable pageable = PageRequest.of(page, 24, sortObj);

        java.util.List<String> sizeParam = (sizes != null && !sizes.isEmpty()) ? sizes : null;
        java.util.List<String> colorParam = (colors != null && !colors.isEmpty()) ? colors : null;
        
        Page<Product> productPage = searchProductsWithFilters(keyword, null, sizeParam, colorParam, minPrice, maxPrice, pageable);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("products", productPage.getContent());
        responseData.put("currentPage", page);
        responseData.put("totalPages", productPage.getTotalPages());
        responseData.put("totalElements", productPage.getTotalElements());

        return responseData;
    }

    @Override
    public java.util.Map<String, Object> getHomeData(java.util.List<com.fashionshop.model.Banner> activeBanners) {
        java.util.List<Product> womenProducts = findTop10NewestWomen();
        java.util.List<Product> menProducts = findTop10NewestMen();

        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("womenProducts", womenProducts);
        data.put("menProducts", menProducts);
        data.put("banners", activeBanners);

        return data;
    }


}
