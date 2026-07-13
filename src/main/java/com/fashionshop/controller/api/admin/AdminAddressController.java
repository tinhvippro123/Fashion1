package com.fashionshop.controller.api.admin;
import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.Address;
import com.fashionshop.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/addresses")
public class AdminAddressController {
    
    @Autowired
    private AddressService addressService;
    
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.success("Xóa địa chỉ thành công");
    }
}
