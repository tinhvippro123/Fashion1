package com.fashionshop.dto.admin;
import jakarta.validation.constraints.NotBlank;
public class OrderUpdateStatusDTO {
    @NotBlank(message = "Trạng thái đơn hàng không được để trống")
    private String status;
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
