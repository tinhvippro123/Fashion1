package com.fashionshop.dto.admin;
import jakarta.validation.constraints.NotBlank;
public class CategoryRequestDTO {
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
