package com.fashionshop.dto.admin;
import jakarta.validation.constraints.NotBlank;
public class UserUpdateRoleDTO {
    @NotBlank(message = "Vai trò không được để trống")
    private String role;
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
