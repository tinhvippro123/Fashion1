package com.fashionshop.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequestDTO {
    private String targetUrl;
    private String position;
    private MultipartFile imageFile;
    private Boolean isActive;
    private Integer displayOrder;










}
