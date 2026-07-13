package com.fashionshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



import jakarta.validation.constraints.NotBlank;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {



    @NotBlank(message = "Tên người nhận không được để trống")

    private String receiverName;



    @NotBlank(message = "Số điện thoại không được để trống")

    private String phone;



    @NotBlank(message = "Tỉnh/Thành phố không được để trống")

    private String province;



    @NotBlank(message = "Quận/Huyện không được để trống")

    private String district;



    @NotBlank(message = "Phường/Xã không được để trống")

    private String ward;



    @NotBlank(message = "Số nhà, tên đường không được để trống")

    private String street;



    private String note;

    private String paymentMethod = "COD";
}

