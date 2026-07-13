package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.admin.ColorRequestDTO;
import com.fashionshop.model.Color;
import com.fashionshop.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/colors")
public class AdminColorController {

    @Autowired
    private ColorService colorService;

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.success(colorService.getAllColors());
    }

    @GetMapping("/{id}")
    public ApiResponse<Color> getColor(@PathVariable Long id) {
        return ApiResponse.success(colorService.getColorById(id));
    }

    @PostMapping
    public ApiResponse<Color> create(@Valid @RequestBody ColorRequestDTO request) {
        return ApiResponse.success(colorService.createColor(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Color> update(@PathVariable Long id, @Valid @RequestBody ColorRequestDTO request) {
        return ApiResponse.success(colorService.updateColor(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        colorService.deleteColor(id);
        return ApiResponse.success("Xóa thành công");
    }
}
