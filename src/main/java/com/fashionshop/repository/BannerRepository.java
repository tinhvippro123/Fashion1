package com.fashionshop.repository;

import com.fashionshop.model.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByIsActiveOrderByDisplayOrderAsc(boolean isActive);
    org.springframework.data.domain.Page<Banner> findByTargetUrlContainingIgnoreCase(String targetUrl, org.springframework.data.domain.Pageable pageable);
}
