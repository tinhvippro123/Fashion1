package com.fashionshop.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColorRequestDTO {
    @NotBlank(message = "Tên màu không được để trống")
    private String name;
    @NotBlank(message = "Mã hex không được để trống")
    private String hexCode;




}
