package com.fashionshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {

    @NotNull(message = "Thiếu mã sản phẩm (variantId)")
    private Long variantId;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private int quantity = 1;

}
