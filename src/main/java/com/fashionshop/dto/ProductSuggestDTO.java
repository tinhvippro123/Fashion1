package com.fashionshop.dto;

public class ProductSuggestDTO {
    private Long id;
    private String name;
    private String slug;
    private Double basePrice;
    private String imageUrl;

    public ProductSuggestDTO() {}

    public ProductSuggestDTO(Long id, String name, String slug, Double basePrice, String imageUrl) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.basePrice = basePrice;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
