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
        return bannerRepository.findAll(Sort.by(Sort.Direction.ASC, "displayOrder"));
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
