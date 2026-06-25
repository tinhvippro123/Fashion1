package com.fashionshop.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fashionshop.dto.ProductSuggestDTO;
import com.fashionshop.model.Product;
import com.fashionshop.service.ProductService;

@RestController
@RequestMapping("/api")
public class SearchApiController {

    @Autowired
    private ProductService productService;

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> liveSearch(@RequestParam("keyword") String keyword) {
        // Fetch top 4 active products matching the keyword
        Page<Product> productPage = productService.searchProductsWithFilters(
                keyword, null, null, null, null, null, PageRequest.of(0, 4));

        List<ProductSuggestDTO> suggestions = productPage.getContent().stream().map(p -> {
            String imageUrl = "";
            if (p.getProductColors() != null && !p.getProductColors().isEmpty() 
                    && p.getProductColors().get(0).getImages() != null 
                    && !p.getProductColors().get(0).getImages().isEmpty()) {
                imageUrl = p.getProductColors().get(0).getImages().get(0).getImageUrl();
            }
            return new ProductSuggestDTO(p.getId(), p.getName(), p.getSlug(), p.getBasePrice(), imageUrl);
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("suggestions", suggestions);
        response.put("totalElements", productPage.getTotalElements());

        return ResponseEntity.ok(response);
    }
}
