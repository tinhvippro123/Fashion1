package com.fashionshop.service.impl;

import com.fashionshop.model.Banner;
import com.fashionshop.repository.BannerRepository;
import com.fashionshop.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Override
    public List<Banner> getActiveBanners() {
        return bannerRepository.findByIsActiveOrderByDisplayOrderAsc(true);
    }

    @Override
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "displayOrder"));
    }

    @Override
    public org.springframework.data.domain.Page<Banner> getAllBanners(org.springframework.data.domain.Pageable pageable) {
        return bannerRepository.findAll(pageable);
    }

    @Override
    public org.springframework.data.domain.Page<Banner> searchBanners(String keyword, org.springframework.data.domain.Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bannerRepository.findAll(pageable);
        }
        return bannerRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Override
    public void saveBanner(Banner banner) {
        bannerRepository.save(banner);
    }

    @Override
    public Banner getBannerById(Long id) {
        return bannerRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }
}
