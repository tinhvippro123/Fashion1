package com.fashionshop.service;

import com.fashionshop.model.Product;
import com.fashionshop.model.User;

import java.util.List;

public interface RecentlyViewedService {
    void addProductToRecentlyViewed(User user, Product product);
    List<Product> getRecentlyViewedProducts(User user, int limit);

    void addProductToRecentlyViewed(String email, Long productId);
    List<Product> getRecentlyViewedProducts(String email, int limit);
}
