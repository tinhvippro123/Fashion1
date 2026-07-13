package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.admin.SizeRequestDTO;
import com.fashionshop.model.Size;
import com.fashionshop.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/sizes")
public class AdminSizeController {

    @Autowired
    private SizeService sizeService;

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.success(sizeService.getAllSizes());
    }

    @GetMapping("/{id}")
    public ApiResponse<Size> getSize(@PathVariable Long id) {
        return ApiResponse.success(sizeService.getSizeById(id));
    }

    @PostMapping
    public ApiResponse<Size> create(@Valid @RequestBody SizeRequestDTO request) {
        return ApiResponse.success(sizeService.createSize(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Size> update(@PathVariable Long id, @Valid @RequestBody SizeRequestDTO request) {
        return ApiResponse.success(sizeService.updateSize(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        sizeService.deleteSize(id);
        return ApiResponse.success("Xóa thành công");
    }
}
