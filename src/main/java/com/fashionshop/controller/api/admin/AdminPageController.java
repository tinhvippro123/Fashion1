package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.admin.PageRequestDTO;
import com.fashionshop.model.Page;
import com.fashionshop.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/pages")
public class AdminPageController {

    @Autowired
    private PageService pageService;

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.success(pageService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Page> getPage(@PathVariable Long id) {
        return ApiResponse.success(pageService.findById(id));
    }

    @PostMapping
    public ApiResponse<Page> create(@Valid @RequestBody PageRequestDTO request) {
        return ApiResponse.success(pageService.createPage(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Page> update(@PathVariable Long id, @Valid @RequestBody PageRequestDTO request) {
        return ApiResponse.success(pageService.updatePage(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        pageService.delete(id);
        return ApiResponse.success("Xóa thành công");
    }
}
