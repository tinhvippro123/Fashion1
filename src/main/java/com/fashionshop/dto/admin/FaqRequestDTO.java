package com.fashionshop.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaqRequestDTO {

    @NotBlank(message = "CÃ¢u há»\ufffdi khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String question;

    @NotBlank(message = "CÃ¢u tráº£ lá»\ufffdi khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String answer;

    @NotNull(message = "Tráº¡ng thÃ¡i hoáº¡t Ä‘á»™ng khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private Boolean isActive;

}

