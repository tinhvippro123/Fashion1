package com.fashionshop.dto.admin;
import jakarta.validation.constraints.NotNull;
public class UserUpdateStatusDTO {
    @NotNull(message = "Trạng thái không được để trống")
    private Boolean isActive;
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
