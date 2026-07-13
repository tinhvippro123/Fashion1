package com.fashionshop.controller.api.admin;
import com.fashionshop.dto.ApiResponse;
import com.fashionshop.model.Contact;
import com.fashionshop.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/contacts")
public class AdminContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping
    public ApiResponse<List<Contact>> list() {
        return ApiResponse.success(contactService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Contact> getContact(@PathVariable Long id) {
        return ApiResponse.success(contactService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        contactService.delete(id);
        return ApiResponse.success("Xóa liên hệ thành công");
    }
}
