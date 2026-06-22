package com.fashionshop.service;

import com.fashionshop.model.Banner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BannerService {
    List<Banner> getActiveBanners();
    List<Banner> getAllBanners();
    Page<Banner> getAllBanners(Pageable pageable);
    Page<Banner> searchBanners(String keyword, Pageable pageable);
    void saveBanner(Banner banner);
    Banner getBannerById(Long Id);
    void deleteBanner(Long id);
}
