package com.fashionshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSuggestDTO {
    private Long id;
    private String name;
    private String slug;
    private Double basePrice;
    private String imageUrl;

}
