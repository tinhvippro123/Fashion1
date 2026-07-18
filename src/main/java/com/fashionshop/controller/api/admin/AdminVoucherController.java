package com.fashionshop.controller.api.admin;

import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.Voucher;
import com.fashionshop.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/vouchers")
public class AdminVoucherController {

    @Autowired
    private VoucherRepository voucherRepository;

    @GetMapping
    public ApiResponse<List<Voucher>> getAllVouchers() {
        return ApiResponse.success(voucherRepository.findAll());
    }

    @PostMapping
    public ApiResponse<Voucher> createVoucher(@RequestBody Voucher voucher) {
        return ApiResponse.success(voucherRepository.save(voucher));
    }
}
