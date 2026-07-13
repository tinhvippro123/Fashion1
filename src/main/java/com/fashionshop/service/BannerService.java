package com.fashionshop.service;

import com.fashionshop.model.Banner;
import com.fashionshop.dto.admin.BannerRequestDTO;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BannerService {
    List<Banner> getActiveBanners();
    List<Banner> getActiveBannersByPosition(String position);
    List<Banner> getAllBanners();
    Page<Banner> getAllBanners(Pageable pageable);
    Page<Banner> searchBanners(String keyword, Pageable pageable);
    Banner saveBanner(Banner banner);
    Banner createBanner(BannerRequestDTO request);
    Banner updateBanner(Long id, BannerRequestDTO request);
    Banner getBannerById(Long Id);
    void deleteBanner(Long id);
}
