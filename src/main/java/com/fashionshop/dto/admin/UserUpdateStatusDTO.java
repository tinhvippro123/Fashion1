package com.fashionshop.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateStatusDTO {
    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive;


}
