package com.fashionshop.service;

import com.fashionshop.model.Banner;

import java.util.List;

public interface BannerService {
    List<Banner> getActiveBanners();
    List<Banner> getAllBanners();
    void saveBanner(Banner banner);
    Banner getBannerById(Long Id);
    void deleteBanner(Long id);
}
