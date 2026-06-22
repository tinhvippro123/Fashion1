package com.fashionshop.controller.admin;

import com.fashionshop.model.Banner;
import com.fashionshop.service.BannerService;
import com.fashionshop.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/banners")
public class AdminBannerController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private StorageService storageService;

    @GetMapping
    public String listBanners(Model model) {
        model.addAttribute("banners", bannerService.getAllBanners());
        return "admin/banners/index";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("banner", new Banner());
        return "admin/banners/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("banner", bannerService.getBannerById(id));
        return "admin/banners/form";
    }

    @PostMapping("/save")
    public String saveBanner(@ModelAttribute Banner banner, @RequestParam(value = "file", required = false) MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String filename = storageService.store(file);
            banner.setImageName(filename);
        } else if (banner.getId() != null) {
            Banner existing = bannerService.getBannerById(banner.getId());
            if (existing != null) {
                banner.setImageName(existing.getImageName());
            }
        }
        bannerService.saveBanner(banner);
        return "redirect:/admin/banners";
    }

    @GetMapping("/delete/{id}")
    public String deleteBanner(@PathVariable("id") Long id) {
        bannerService.deleteBanner(id);
        return "redirect:/admin/banners";
    }
}
