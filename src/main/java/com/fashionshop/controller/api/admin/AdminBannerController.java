package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.admin.BannerRequestDTO;
import com.fashionshop.model.Banner;
import com.fashionshop.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/banners")
public class AdminBannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public ApiResponse<List<Banner>> list() {
        return ApiResponse.success(bannerService.getAllBanners());
    }

    @GetMapping("/{id}")
    public ApiResponse<Banner> getBanner(@PathVariable Long id) {
        return ApiResponse.success(bannerService.getBannerById(id));
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<Banner> create(@Valid @ModelAttribute BannerRequestDTO request) {
        return ApiResponse.success(bannerService.createBanner(request));
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ApiResponse<Banner> update(@PathVariable Long id, @Valid @ModelAttribute BannerRequestDTO request) {
        return ApiResponse.success(bannerService.updateBanner(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ApiResponse.success("Xóa banner thành công");
    }
}
