package com.fashionshop.service.impl;

import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.model.Product;
import com.fashionshop.model.RecentlyViewedItem;
import com.fashionshop.model.User;
import com.fashionshop.repository.ProductRepository;
import com.fashionshop.repository.RecentlyViewedItemRepository;
import com.fashionshop.repository.UserRepository;
import com.fashionshop.service.RecentlyViewedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecentlyViewedServiceImpl implements RecentlyViewedService {

    @Autowired
    private RecentlyViewedItemRepository recentlyViewedItemRepository;

    private static final int MAX_RECENTLY_VIEWED_ITEMS = 40;

    @Override
    @Transactional
    public void addProductToRecentlyViewed(User user, Product product) {
        Optional<RecentlyViewedItem> existingItem = recentlyViewedItemRepository.findByUserIdAndProductId(user.getId(), product.getId());

        if (existingItem.isPresent()) {
            // Update time
            RecentlyViewedItem item = existingItem.get();
            // onUpdate() in entity will update viewedAt
            recentlyViewedItemRepository.save(item);
        } else {
            long count = recentlyViewedItemRepository.countByUserId(user.getId());
            if (count >= MAX_RECENTLY_VIEWED_ITEMS) {
                // Delete oldest
                Optional<RecentlyViewedItem> oldestItem = recentlyViewedItemRepository.findFirstByUserIdOrderByViewedAtAsc(user.getId());
                oldestItem.ifPresent(item -> recentlyViewedItemRepository.delete(item));
            }

            RecentlyViewedItem newItem = new RecentlyViewedItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            recentlyViewedItemRepository.save(newItem);
        }
    }

    @Override
    public List<Product> getRecentlyViewedProducts(User user, int limit) {
        List<RecentlyViewedItem> items = recentlyViewedItemRepository.findByUserIdOrderByViewedAtDesc(user.getId(), PageRequest.of(0, limit));
        return items.stream().map(RecentlyViewedItem::getProduct).collect(Collectors.toList());
    }

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Override
    public void addProductToRecentlyViewed(String email, Long productId) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new FashionShopException(ErrorCode.USER_NOT_FOUND);
        Product product = productRepository.findById(productId).orElseThrow(() -> new FashionShopException(ErrorCode.PRODUCT_NOT_FOUND));
        addProductToRecentlyViewed(user, product);
    }

    @Override
    public List<Product> getRecentlyViewedProducts(String email, int limit) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new FashionShopException(ErrorCode.USER_NOT_FOUND);
        return getRecentlyViewedProducts(user, limit);
    }
}
