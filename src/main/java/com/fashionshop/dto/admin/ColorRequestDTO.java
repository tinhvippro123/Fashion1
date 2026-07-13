package com.fashionshop.dto.admin;
import jakarta.validation.constraints.NotBlank;
public class ColorRequestDTO {
    @NotBlank(message = "Tên màu không được để trống")
    private String name;
    @NotBlank(message = "Mã hex không được để trống")
    private String hexCode;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHexCode() { return hexCode; }
    public void setHexCode(String hexCode) { this.hexCode = hexCode; }
}
