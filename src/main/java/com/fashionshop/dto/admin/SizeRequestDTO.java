package com.fashionshop.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SizeRequestDTO {
    @NotBlank(message = "Tên kích thước không được để trống")
    private String name;


}
