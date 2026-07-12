package com.fashionshop.controller.api;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.Product;
import com.fashionshop.service.BannerService;
import com.fashionshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController("apiHomeController")
@RequestMapping("/api/v1/home")
public class HomeController {

    @Autowired
    private ProductService productService;

    @Autowired
    private BannerService bannerService;

    @GetMapping
    public ApiResponse<Map<String, Object>> getHomeData() {
        List<Product> womenProducts = productService.findTop10NewestWomen();
        List<Product> menProducts = productService.findTop10NewestMen();
        var banners = bannerService.getActiveBanners();

        Map<String, Object> data = new HashMap<>();
        data.put("womenProducts", womenProducts);
        data.put("menProducts", menProducts);
        data.put("banners", banners);

        return ApiResponse.success(data);
    }
}
