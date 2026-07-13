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

    @NotBlank(message = "TiÃªu đỏ không được để trống")

    private String title;

    @NotBlank(message = "Slug không được để trống")

    private String slug;

    @NotBlank(message = "Ná»™i dung không được để trống")

    private String content;

    @NotNull(message = "Trạng thái hoạt động không được để trống")

    private Boolean isActive;

}

