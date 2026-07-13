package com.fashionshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.NotNull;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartRequest {



    @NotNull(message = "Thiáº¿u mÃ£ sáº£n pháº©m trong giá»\ufffd (itemId)")

    private Long itemId;



    @Min(value = 1, message = "Sá»‘ lÆ°á»£ng pháº£i lá»›n hÆ¡n 0")

    private int quantity;

}

