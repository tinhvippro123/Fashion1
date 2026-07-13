package com.fashionshop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;



import jakarta.validation.constraints.NotBlank;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {



    @NotBlank(message = "TÃªn ngÆ°á»\ufffdi nháº­n khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String receiverName;



    @NotBlank(message = "Sá»‘ Ä‘iá»‡n thoáº¡i khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String phone;



    @NotBlank(message = "Tá»‰nh/ThÃ nh phá»‘ khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String province;



    @NotBlank(message = "Quáº­n/Huyá»‡n khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String district;



    @NotBlank(message = "PhÆ°á»\ufffdng/XÃ£ khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String ward;



    @NotBlank(message = "Sá»‘ nhÃ , tÃªn Ä‘Æ°á»\ufffdng khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String street;



    private String note;

    private String paymentMethod = "COD";
}

