package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.admin.FaqRequestDTO;
import com.fashionshop.model.Faq;
import com.fashionshop.service.FaqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/faqs")
public class AdminFaqController {

    @Autowired
    private FaqService faqService;

    @GetMapping
    public ApiResponse<?> list() {
        return ApiResponse.success(faqService.getAllFaqs());
    }

    @GetMapping("/{id}")
    public ApiResponse<Faq> getFaq(@PathVariable Long id) {
        return ApiResponse.success(faqService.getFaqById(id));
    }

    @PostMapping
    public ApiResponse<Faq> create(@Valid @RequestBody FaqRequestDTO request) {
        return ApiResponse.success(faqService.createFaq(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Faq> update(@PathVariable Long id, @Valid @RequestBody FaqRequestDTO request) {
        return ApiResponse.success(faqService.updateFaq(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ApiResponse.success("Xóa thành công");
    }
}
