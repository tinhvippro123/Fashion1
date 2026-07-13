package com.fashionshop.controller.api.admin;
import com.fashionshop.dto.ApiResponse;
import com.fashionshop.dto.admin.UserUpdateRoleDTO;
import com.fashionshop.dto.admin.UserUpdateStatusDTO;
import com.fashionshop.enums.UserRole;
import com.fashionshop.model.User;
import com.fashionshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<Page<User>> list(@RequestParam(defaultValue = "0") int page) {
        return ApiResponse.success(userService.getAllUsers(PageRequest.of(page, 10)));
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PutMapping("/{id}/role")
    public ApiResponse<String> updateRole(@PathVariable Long id, @Valid @RequestBody UserUpdateRoleDTO request) {
        userService.updateUserRole(id, UserRole.valueOf(request.getRole()));
        return ApiResponse.success("Cập nhật vai trò thành công");
    }

    @PutMapping("/{id}/status")
    public ApiResponse<String> updateStatus(@PathVariable Long id, @Valid @RequestBody UserUpdateStatusDTO request) {
        userService.toggleUserStatus(id, request.getIsActive());
        return ApiResponse.success("Cập nhật trạng thái thành công");
    }
}
