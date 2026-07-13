package com.fashionshop.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    private String description;
    @NotNull(message = "Giá cơ bản không được để trống")
    @Min(value = 0, message = "Giá cơ bản phải lớn hơn hoặc bằng 0")
    private Double basePrice;
    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;








}
