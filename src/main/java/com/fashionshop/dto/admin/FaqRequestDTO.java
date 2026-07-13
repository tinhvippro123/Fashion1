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

    @NotBlank(message = "CÃ¢u hỏi không được để trống")

    private String question;

    @NotBlank(message = "CÃ¢u trả lỏi không được để trống")

    private String answer;

    @NotNull(message = "Trạng thái hoạt động không được để trống")

    private Boolean isActive;

}

