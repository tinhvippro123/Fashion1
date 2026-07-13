package com.fashionshop.service.impl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fashionshop.model.Banner;
import com.fashionshop.repository.BannerRepository;
import com.fashionshop.dto.admin.BannerRequestDTO;
import com.fashionshop.exception.ErrorCode;
import com.fashionshop.exception.FashionShopException;
import com.fashionshop.service.StorageService;
import com.fashionshop.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerServiceImpl implements BannerService {

    @Autowired
    private BannerRepository bannerRepository;

	@Autowired
	private com.fashionshop.service.StorageService storageService;

    @Override
    public List<Banner> getActiveBanners() {
        return bannerRepository.findByIsActiveOrderByDisplayOrderAsc(true);
    }

    @Override
    public List<Banner> getActiveBannersByPosition(String position) {
        return bannerRepository.findByPositionAndIsActiveOrderByDisplayOrderAsc(position, true);
    }

    @Override
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll(Sort.by(Sort.Direction.ASC, "displayOrder"));
    }

    @Override
    public Page<Banner> getAllBanners(Pageable pageable) {
        return bannerRepository.findAll(pageable);
    }

    @Override
    public Page<Banner> searchBanners(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return bannerRepository.findAll(pageable);
        }
        return bannerRepository.findByTargetUrlContainingIgnoreCase(keyword.trim(), pageable);
    }

    @Override
    public Banner saveBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    @Override
    public Banner getBannerById(Long id) {
        return bannerRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    @Override
    public Banner createBanner(BannerRequestDTO request) {
        Banner banner = new Banner();
        banner.setTargetUrl(request.getTargetUrl());
        banner.setPosition(request.getPosition() != null ? request.getPosition() : "HOME_MAIN");
        banner.setActive(request.getIsActive() != null ? request.getIsActive() : true);
        banner.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            String imageUrl = storageService.store(request.getImageFile());
            banner.setImageName(imageUrl);
        }
        
        return bannerRepository.save(banner);
    }

    @Override
    public Banner updateBanner(Long id, BannerRequestDTO request) {
        Banner banner = getBannerById(id);
        banner.setTargetUrl(request.getTargetUrl());
        banner.setPosition(request.getPosition() != null ? request.getPosition() : "HOME_MAIN");
        if (request.getIsActive() != null) banner.setActive(request.getIsActive());
        if (request.getDisplayOrder() != null) banner.setDisplayOrder(request.getDisplayOrder());
        
        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            if (banner.getImageName() != null) {
                storageService.delete(banner.getImageName());
            }
            String imageUrl = storageService.store(request.getImageFile());
            banner.setImageName(imageUrl);
        }
        
        return bannerRepository.save(banner);
    }
}
