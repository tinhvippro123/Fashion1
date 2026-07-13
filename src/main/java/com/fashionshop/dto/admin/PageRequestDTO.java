package com.fashionshop.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @NotBlank(message = "TiÃªu Ä‘á»\ufffd khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String title;

    @NotBlank(message = "Slug khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String slug;

    @NotBlank(message = "Ná»™i dung khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private String content;

    @NotNull(message = "Tráº¡ng thÃ¡i hoáº¡t Ä‘á»™ng khÃ´ng Ä‘Æ°á»£c Ä‘á»ƒ trá»‘ng")

    private Boolean isActive;

}

