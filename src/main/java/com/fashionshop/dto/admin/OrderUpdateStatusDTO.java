package com.fashionshop.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateStatusDTO {
    @NotBlank(message = "Trạng thái đơn hàng không được để trống")
    private String status;


}
