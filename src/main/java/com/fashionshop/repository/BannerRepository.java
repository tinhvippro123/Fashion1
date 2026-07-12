package com.fashionshop.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fashionshop.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByIsActiveOrderByDisplayOrderAsc(boolean isActive);
    List<Banner> findByPositionAndIsActiveOrderByDisplayOrderAsc(String position, boolean isActive);
    Page<Banner> findByTargetUrlContainingIgnoreCase(String targetUrl, Pageable pageable);
}
