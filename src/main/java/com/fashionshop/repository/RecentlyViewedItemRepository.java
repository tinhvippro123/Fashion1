package com.fashionshop.repository;

import com.fashionshop.model.RecentlyViewedItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentlyViewedItemRepository extends JpaRepository<RecentlyViewedItem, Long> {

    List<RecentlyViewedItem> findByUserIdOrderByViewedAtDesc(Long userId, Pageable pageable);

    Optional<RecentlyViewedItem> findByUserIdAndProductId(Long userId, Long productId);

    long countByUserId(Long userId);
    
    // Tìm phần tử cũ nhất (hoặc tất cả các phần tử trừ 40 phần tử mới nhất) thì hơi phức tạp bằng query method.
    // Ta có thể dùng findByUserIdOrderByViewedAtAsc để lấy và xoá cái cũ nhất
    Optional<RecentlyViewedItem> findFirstByUserIdOrderByViewedAtAsc(Long userId);
}
